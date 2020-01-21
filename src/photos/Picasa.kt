package photos

import com.google.appengine.api.ThreadManager
import integration.*
import java.lang.Math.min
import java.lang.System.currentTimeMillis
import java.net.URL
import java.security.SecureRandom
import java.util.*
import kotlin.concurrent.thread

class Picasa(
  private val auth: OAuth,
  private val localContent: LocalContent?,
  private val jsonLoader: JsonLoader = JsonLoader()
) {
  companion object {
    internal var random: Random = SecureRandom()
    private val instances = mutableMapOf<OAuth, Picasa>()

    fun of(auth: OAuth, localContent: LocalContent?) = instances.computeIfAbsent(auth) {
      Picasa(auth, localContent)
    }
  }

  val urlPrefix get() = "/${auth.profile?.slug ?: ""}"
  val urlSuffix get() = if (auth.isDefault) "" else "?by=${auth.profile?.slug}"

  val gallery = (if (localContent != null)
      jsonLoader.loadAll(auth, "/v1/albums", AlbumsResponse::class)
    else
      jsonLoader.loadAll(auth, "/v1/sharedAlbums", SharedAlbumsResponse::class)
    ).toGallery().also { loadThumbs(it.albums.values) }

  private fun loadThumbs(albums: Iterable<Album>) = ThreadManager.createBackgroundThread {
    albums.forEach { album ->
      album.thumbContent = URL(album.thumbUrl).openConnection().getInputStream().use { it.readBytes() }
    }
  }.start()

  fun getAlbumPhotos(album: Album, pageToken: String?) = Cache.get(album.name + ":" + album.id + ":" + pageToken) {
    val photos = jsonLoader.load(auth, "/v1/mediaItems:search", PhotosResponse::class, mapOf("albumId" to album.id, "pageToken" to pageToken))
    AlbumPart(photos.mediaItems.toPhotos(), photos.nextPageToken)
  }

  fun getAlbumPhotos(album: Album, upToIndex: Int = Int.MAX_VALUE): List<Photo> {
    var pageToken: String? = null
    val photos = mutableListOf<Photo>()
    do {
      val albumPart = getAlbumPhotos(album, pageToken)
      pageToken = albumPart.nextPageToken
      photos += albumPart.photos
    } while (pageToken != null && photos.size < upToIndex)
    return photos
  }

  fun findAlbumPhoto(album: Album, photoIdxOrId: String): Photo? {
    var pageToken: String? = null
    val photoIdx = photoIdxOrId.toIntOrNull()
    do {
      val albumPart = getAlbumPhotos(album, pageToken)
      pageToken = albumPart.nextPageToken
      if (photoIdx != null && albumPart.photos.size > photoIdx) return albumPart.photos[photoIdx - 1]
      albumPart.photos.find { it.id == photoIdxOrId }?.let { return it }
    } while (pageToken != null)
    return null
  }

  fun getRandomPhotos(numNext: Int): RandomPhotos {
    val album = weightedRandom(gallery.albums.values)
    val index = random(album.size)
    val upToIndex = min(index + numNext, album.size)
    val photos = getAlbumPhotos(album, upToIndex)
    return RandomPhotos(photos.subList(index, upToIndex), album.title, auth.profile!!)
  }

  fun weightedRandom(albums: Collection<Album>): Album {
    var sum = 0
    for (album in albums) sum += transform(album.size().toDouble())
    val index = random(sum)

    sum = 0
    for (album in albums) {
      sum += transform(album.size().toDouble())
      if (sum > index) return album
    }
    return albums.first()
  }

  private fun transform(n: Double): Int {
    return (100.0 * Math.log10(1 + n / 50.0)).toInt()
  }

  internal fun random(max: Int): Int {
    return if (max == 0) 0 else random.nextInt(max)
  }

  fun search(query: String): Album {
    return Album().apply {
      // TODO: there is no way to search for text currently... https://developers.google.com/photos/library/reference/rest/v1/mediaItems/search#Filters
      // photos += jsonLoader.loadAll("/v1/mediaItems:search", PhotosResponse::class).toPhotos()
    }
  }

  private fun List<JsonAlbum>.toGallery() = Gallery(asSequence()
    .filter { localContent == null || localContent.contains(it.name) }
    .filter { it.title != null && it.mediaItemsCount > 1 }
    .map {
      val albumContent = localContent?.forAlbum(it.name)
      it.name to Album(it.id, it.name, it.title, albumContent?.content).apply {
        geo = albumContent?.geo
        baseUrl = it.coverPhotoBaseUrl
        size = it.mediaItemsCount
        timestamp = currentTimeMillis()
      }
    }.toMap())

  private fun List<JsonMediaItem>.toPhotos() = map {
    Photo().apply {
      id = it.id
      width = it.mediaMetadata?.width
      height = it.mediaMetadata?.height
      baseUrl = it.baseUrl
      description = it.description
      timestampISO = it.mediaMetadata?.creationTime
      it.mediaMetadata?.photo?.let {
        exif = Exif(it.cameraModel, it.apertureFNumber, it.exposureTime, it.focalLength, it.isoEquivalent)
      }
    }
  }
}
