package net.azib.photos

import java.text.SimpleDateFormat

class Photo : Entity() {
  var width: Int? = null
  var height: Int? = null
  var url: String? = null
  var exif = Exif()

  val dateTime: String
    get() = SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp)

  val thumbUrlLarge: String?
    get() = thumbUrl?.replace("/s\\d+-c/".toRegex(), "/s1024/")

  override var description: String?
    get() = super.description
    set(value) {
      // remove filename-like descriptions that don't make any sense
      super.description = if (value?.matches("(IMG|DSC)?[0-9-_.]+".toRegex()) ?: false) null else value
    }
}
