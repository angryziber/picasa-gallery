package integration

import com.google.gson.Gson
import photos.Config
import kotlin.reflect.KClass
import kotlin.system.measureNanoTime

class JsonLoader(private val http: Http = Http()) {
  private val gson = Gson()

  fun <T, R: JsonResponse<T>> load(auth: OAuth, url: String, responseType: KClass<out R>, params: Map<String, Any?>): R = logTime("$url $params loaded") {
    val fullUrl = (Config.apiBase.takeUnless { url.startsWith("http") } ?: "") + url
    val request = if (url.endsWith("search")) http.send(auth, fullUrl, gson.toJson(params))
                  else http.send(auth, fullUrl + params.toUrl())
    request.use { gson.fromJson(it.bufferedReader(), responseType.java) }
  }

  fun <T, R: JsonResponse<T>> loadAll(auth: OAuth, url: String, responseType: KClass<out R>, params: Map<String, Any?> = emptyMap()): List<T> {
    @Suppress("NAME_SHADOWING") val params = HashMap(params)
    params["pageSize"] = if (url.contains("albums", ignoreCase = true)) 50 else 100
    var response = load(auth, url, responseType, params)
    val items = ArrayList(response.items)
    while (response.nextPageToken != null) {
      params["pageToken"] = response.nextPageToken
      response = load(auth, url, responseType, params)
      items += response.items
    }
    return items
  }

  private fun Map<String, Any?>.toUrl() = "?" + entries.joinToString("&") { "${it.key}=${it.value}" }
}

abstract class JsonResponse<T> {
  abstract val items: List<T>
  var nextPageToken: String? = null
}

data class Profile(var id: String? = null, var name: String? = null, var picture: String? = null): JsonResponse<Profile>() {
  override val items get() = listOf(this)
  val slug get() = name?.toLowerCase()?.replace(' ', '.')
}

class AlbumsResponse: JsonResponse<JsonAlbum>() {
  var albums: List<JsonAlbum> = mutableListOf()
  override val items: List<JsonAlbum> get() = albums
}

class SharedAlbumsResponse: JsonResponse<JsonAlbum>() {
  var sharedAlbums: List<JsonAlbum> = mutableListOf()
  override val items: List<JsonAlbum> get() = sharedAlbums
}

data class JsonAlbum(
  var id: String = "",
  var title: String? = null,
  var productUrl: String? = null,
  var shareInfo: JsonAlbumShareInfo? = null,
  var mediaItemsCount: Int = 0,
  var coverPhotoBaseUrl: BaseUrl = BaseUrl("")
) {
  val name get() = (title ?: id).replace("[^\\d\\w]".toRegex(), "")
}

data class JsonAlbumShareInfo(
  var shareableUrl: String? = null,
  var shareToken: String? = null,
  var sharedAlbumOptions: JsonSharedAlbumOptions? = null
)

data class JsonSharedAlbumOptions(var isCollaborative: Boolean, var isCommentable: Boolean)

class PhotosResponse: JsonResponse<JsonMediaItem>() {
  var mediaItems: List<JsonMediaItem> = mutableListOf()
  override val items: List<JsonMediaItem> get() = mediaItems
}

data class JsonMediaItem(
  var id: String = "",
  var description: String? = null,
  var baseUrl: BaseUrl = BaseUrl(""),
  var productUrl: String? = null,
  var filename: String = "",
  var mediaMetadata: MediaMetadata? = null
)

data class MediaMetadata(
  val creationTime: String = "",
  val width: Int = 0,
  val height: Int = 0,
  val photo: PhotoMetadata? = null
)

data class PhotoMetadata(
  val cameraMake: String? = null,
  val cameraModel: String? = null,
  val focalLength: String? = null,
  val apertureFNumber: String? = null,
  val isoEquivalent: Int? = null,
  val exposureTime: String? = null
)

inline class BaseUrl(val url: String) {
  fun fit(w: Int, h: Int) = "$url=w$w-h$h"
  fun crop(s: Int) = fit(s, s) + "-c"
}

fun main() {
  println(measureNanoTime {
    val albums = JsonLoader().loadAll(OAuth.default, "/v1/albums", AlbumsResponse::class)
    println(albums.size)
    println(albums)
  } / 1000_000)

  println(measureNanoTime {
    val photos = JsonLoader().loadAll(OAuth.default, "/v1/mediaItems:search", PhotosResponse::class, mapOf("albumId" to "ANEKkbUIzG8mAO4pnPWN4bl97MlZrXEBLAA0FBmZ9Fb2PJOug16HvXiw0c4BBBZOxdt24gS1o5Jd"))
    println(photos.size)
    println(photos)
  } / 1000_000)
}
