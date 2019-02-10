package photos

import java.text.SimpleDateFormat

class Photo : Entity() {
  var width: Int? = null
  var height: Int? = null
  var exif = Exif()
  var albumId: String? = null

  val dateTime get() = SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp)

  val date get() = SimpleDateFormat("yyyy-MM-dd").format(timestamp)

  val thumbUrl get() = baseUrl?.crop(144)

  val fullHdUrl get() = baseUrl?.fit(1920, 1080)

  override var description: String?
    get() = super.description
    set(value) {
      // remove filename-like descriptions that don't make any sense
      super.description = if (value?.matches("(IMG|DSC)?[0-9-_.]+".toRegex()) ?: false) null else value
    }
}
