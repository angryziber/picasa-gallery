package photos

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

open class Album(
    id: String? = null,
    name: String? = null,
    title: String? = null,
    var content: String? = null
) : Entity(id, title) {

  companion object {
    val descriptionRegex = Regex("<h2>(.*?)</h2>")
  }

  var size = 0

  val contentIsLong get() = (content?.length ?: 0) > 300

  val description = content?.let { descriptionRegex.find(it)?.groups?.get(1)?.value } ?: ""

  var name = name
    set(value) { if (value != id) field = value }

  override var title: String?
    get() = super.title
    set(value) {
      super.title = value
      if (name == null) name = value?.replace(" ", "")
    }

  var comments: List<Comment> = emptyList()

  val thumbUrl: String?
    get() = baseUrl?.crop(212)

  open fun size() = size
}

data class AlbumPart(val photos: List<Photo>, val nextPageToken: String?) {
  val loadedAt = Date()
}

typealias Gallery = Map<String, Album>