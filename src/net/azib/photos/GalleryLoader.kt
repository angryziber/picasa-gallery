package net.azib.photos

import java.text.SimpleDateFormat

class GalleryLoader : XMLListener<Gallery> {
  override var result = Gallery()
  private var album: Album? = null

  override fun value(path: String, value: String) {
    when (path) {
      "nickname" -> result.author = value
      "updated" -> result.timestamp = parseTimestamp(value)

      "entry/name" -> album?.name = value
      "entry/title" -> album?.title = value
      "entry/summary" -> album?.description = value
      "entry/nickname" -> album?.author = value
      "entry/access" -> album?.isPublic = "public" == value
      "entry/timestamp" -> album?.timestamp = value.toLong()
      "entry/group/thumbnail@url" -> album?.thumbUrl = value
      "entry/where/Point/pos" -> album?.geo = GeoLocation(value)
      "entry/numphotos" -> album?.size = value.toInt()
    }
  }

  private fun parseTimestamp(value: String) =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(value).time

  override fun start(path: String) {
    if ("entry" == path) {
      album = Album()
      result.albums.add(album!!)
    }
  }
}
