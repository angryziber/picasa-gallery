package integration

import com.google.gson.Gson
import photos.Config
import kotlin.reflect.KClass
import kotlin.system.measureNanoTime

class JsonLoader(private val http: Http = Http()) {
  private val gson = Gson()

  fun <T, R: JsonResponse<T>> load(url: String, responseType: KClass<out R>, params: Map<String, Any?>): R {
    val fullUrl = (Config.apiBase.takeUnless { url.startsWith("http") } ?: "") + url
    val request = if (url.endsWith("search")) http.send(fullUrl, gson.toJson(params))
                  else http.send(fullUrl + params.toUrl())

    return request.use {
      gson.fromJson(it.bufferedReader(), responseType.java)
    }
  }

  fun <T, R: JsonResponse<T>> loadAll(url: String, responseType: KClass<out R>, params: Map<String, Any?> = emptyMap()): List<T> {
    @Suppress("NAME_SHADOWING") val params = HashMap(params)
    params["pageSize"] = if (url.contains("albums")) 50 else 100
    var response = load(url, responseType, params)
    val items = ArrayList(response.items)
    while (response.nextPageToken != null) {
      params["pageToken"] = response.nextPageToken
      response = load(url, responseType, params)
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

data class Profile(var id: String? = null, var name: String? = null, var link: String? = null, var picture: String? = null): JsonResponse<Profile>() {
  override val items get() = listOf(this)
  val slug get() = name?.toLowerCase()?.replace(' ', '.')
}

class AlbumsResponse: JsonResponse<JsonAlbum>() {
  var albums: List<JsonAlbum> = mutableListOf()
  override val items: List<JsonAlbum> get() = albums
}

data class JsonAlbum(
  var id: String = "",
  var title: String? = null,
  var productUrl: String? = null,
  var mediaItemsCount: Int = 0,
  var coverPhotoBaseUrl: BaseUrl = BaseUrl("")
) {
  val name: String? get() = title?.replace("[^\\d\\w]".toRegex(), "")
}

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
  val focalLength: Float? = null,
  val apertureFNumber: Float? = null,
  val isoEquivalent: Int? = null,
  val exposureTime: Float? = null
)

inline class BaseUrl(val url: String) {
  fun fit(w: Int, h: Int) = "$url=w$w-h$h"
  fun crop(s: Int) = fit(s, s) + "-c"
}

fun main() {
  println(measureNanoTime {
    val albums = JsonLoader().loadAll("/v1/albums", AlbumsResponse::class)
    println(albums.size)
    println(albums)
  } / 1000_000)

  println(measureNanoTime {
    val photos = JsonLoader().loadAll("/v1/mediaItems:search", PhotosResponse::class, mapOf("albumId" to "ANEKkbUIzG8mAO4pnPWN4bl97MlZrXEBLAA0FBmZ9Fb2PJOug16HvXiw0c4BBBZOxdt24gS1o5Jd"))
    println(photos.size)
    println(photos)
  } / 1000_000)
}
