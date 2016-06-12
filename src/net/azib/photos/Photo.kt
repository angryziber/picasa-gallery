package net.azib.photos

import java.text.SimpleDateFormat

class Photo : Entity() {
  var id: String? = null
  var width: Int? = null
  var height: Int? = null
  var url: String? = null
  var exif = Exif()

  val dateTime: String
    get() = SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp)

  override var description: String?
    get() = super.description
    set(value) {
      // remove filename-like descriptions that don't make any sense
      super.description = if (value?.matches("(IMG|DSC)?[0-9-_.]+".toRegex()) ?: false) null else value
    }
}
