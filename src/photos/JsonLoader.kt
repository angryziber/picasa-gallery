package photos

import com.google.gson.Gson
import kotlin.reflect.KClass

class JsonLoader(val http: Http = Http()) {
  private val gson = Gson()

  fun <T, R: JsonResponse<T>> load(url: String, responseType: KClass<out R>) = http.get(url).use {
    gson.fromJson(it.bufferedReader(), responseType.java)
  }

  fun <T, R: JsonResponse<T>> loadAll(url: String, responseType: KClass<out R>): List<T> {
    val pagedUrl = "$url?pageSize=50"
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

class JsonAlbum {
  var id = ""
  var title: String? = null
  var mediaItemsCount = 0
  var coverPhotoBaseUrl = ""
}