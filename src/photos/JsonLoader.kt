package photos

import com.google.gson.Gson
import kotlin.reflect.KClass
import kotlin.system.measureNanoTime

class JsonLoader(private val http: Http = Http()) {
  private val gson = Gson()

  fun <T, R: JsonResponse<T>> load(url: String, responseType: KClass<out R>) = http.get(url).use {
    gson.fromJson(it.bufferedReader(), responseType.java)
  }

  fun <T, R: JsonResponse<T>> loadAll(url: String, responseType: KClass<out R>): List<T> {
    val pagedUrl = "$url?pageSize=" + if (url.contains("albums")) 50 else 100
    var response = load(pagedUrl, responseType)
    val items = ArrayList(response.items)
    while (response.nextPageToken != null) {
      response = load(pagedUrl + "&pageToken=" + response.nextPageToken, responseType)
      items += response.items
    }
    return items
  }
}

abstract class JsonResponse<T> {
  abstract val items: List<T>
  var nextPageToken: String? = null
}

class AlbumsResponse: JsonResponse<JsonAlbum>() {
  var albums: List<JsonAlbum> = mutableListOf()
  override val items: List<JsonAlbum> get() = albums
}

data class JsonAlbum(
  var id: String = "",
  var title: String? = null,
  var mediaItemsCount: Int = 0,
  var coverPhotoBaseUrl: BaseUrl = BaseUrl("")
)

inline class BaseUrl(val url: String) {
  fun fit(w: Int, h: Int) = "$url=w$w-h$h"
  fun crop(s: Int) = fit(s, s) + "-c"
}

fun main() {
  println(measureNanoTime {
    val albums = JsonLoader().loadAll(Config.apiBase + "/v1/albums", AlbumsResponse::class)
    println(albums.size)
    println(albums)
  } / 1000_000)
}