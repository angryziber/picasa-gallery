package photos

import integration.*
import java.lang.Math.min
import java.security.SecureRandom
import java.util.*

class Picasa(
  private val auth: OAuth,
  private val content: LocalContent,
  private val jsonLoader: JsonLoader = JsonLoader()
) {
  companion object {
    internal var random: Random = SecureRandom()
  }

  val urlPrefix get() = "/${auth.profile?.slug ?: ""}"
  val urlSuffix get() = if (auth.isDefault) "" else "?by=${auth.profile?.slug}"

  val gallery get() = Cache.get("gallery") {
    jsonLoader.loadAll(auth, "/v1/albums", AlbumsResponse::class).toGallery()
  }

  fun getAlbum(name: String): Album {
    val album = gallery.albums[name]!!
    album.photos += Cache.get(album.id!!) {
      jsonLoader.loadAll(auth, "/v1/mediaItems:search", PhotosResponse::class, mapOf("albumId" to album.id)).toPhotos()
    }
    return album
  }

  fun getRandomPhotos(numNext: Int): RandomPhotos {
    val album = weightedRandom(gallery.albums.values)
    val photos = getAlbum(album.name!!).photos
    val index = random(photos.size)
    return RandomPhotos(photos.subList(index, min(index + numNext, photos.size)), album.author, album.title)
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

  private fun List<JsonAlbum>.toGallery() = Gallery(212).apply {
    author = auth.profile?.name
    authorId = auth.profile?.slug
    albums.putAll(filter { content.contains(it.name) }.map {
      val albumContent = content.forAlbum(it.name)
      it.name!! to Album(it.id, it.name, it.title, null, albumContent?.content, author).apply {
        geo = albumContent?.geo
        baseUrl = it.coverPhotoBaseUrl
        size = it.mediaItemsCount
      }
    }.toMap())
  }

  private fun List<JsonMediaItem>.toPhotos() = map {
    Photo().apply {
      id = it.id
      width = it.mediaMetadata?.width
      height = it.mediaMetadata?.height
      baseUrl = it.baseUrl
      description = it.description
      timestampISO = it.mediaMetadata?.creationTime
      it.mediaMetadata?.photo?.let {
        exif = Exif().apply {
          camera = it.cameraModel
          exposure = it.exposureTime
          focal = it.focalLength
          iso = it.isoEquivalent
          fstop = it.apertureFNumber
        }
      }
    }
  }
}
