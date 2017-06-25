package photos

import photos.Album.Access.public
import util.XMLListener
import java.text.SimpleDateFormat
import java.util.*

class GalleryLoader(val contentLoader: ContentLoader, thumbSize: Int) : XMLListener<Gallery> {
  override val result = Gallery(thumbSize)
  private var album = Album()
  private var albumType = ""

  private val datePattern = "\\d{4}-\\d{2}-\\d{2}".toRegex()
  private val timestampFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").apply {
    timeZone = TimeZone.getTimeZone("UTC")
  }

  private fun parseTimestamp(value: String) = timestampFormat.parse(value).time

  override fun value(path: String, value: String) {
    when (path) {
      "user" -> result.authorId = value
      "nickname" -> result.author = value
      "updated" -> result.timestamp = parseTimestamp(value)
    }

    album.apply {
      when (path) {
        "entry/id" -> id = value
        "entry/name" -> name = value
        "entry/title" -> title = value
        "entry/summary" -> description = value
        "entry/nickname" -> author = value
        "entry/access" -> access = Album.Access.valueOf(value)
        "entry/updated" -> timestamp = parseTimestamp(value)
        "entry/group/thumbnail@url" -> thumbUrl = value + ".jpg"
        "entry/where/Point/pos" -> geo = GeoLocation(value)
        "entry/numphotos" -> size = value.toInt()
        "entry/albumType" -> albumType = value
      }
    }
  }

  override fun end(path: String) {
    if ("entry" == path) {
      if (!skip(album)) result += album
      album = Album()
      albumType = ""
    }
  }

  internal fun skip(album: Album): Boolean {
    if (contentLoader.contains(album.name)) return false
    return albumType.isNotEmpty() || // skip ProfilePhotos and Buzz (shared on Maps)
        album.size == 1 && album.name?.matches(datePattern) ?: false ||
        album.access != public
  }
}
