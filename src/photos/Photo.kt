package photos

import java.text.SimpleDateFormat

class Photo : Entity() {
  var width: Int? = null
  var height: Int? = null
  var exif: Exif? = null

  val dateTime get() = SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp)

  val date get() = SimpleDateFormat("yyyy-MM-dd").format(timestamp)

  val thumbUrl get() = baseUrl?.crop(144)

  val fullHdUrl get() = baseUrl?.fit(1920, 1080)

  var description: String? = null
    set(value) {
      // remove filename-like descriptions that don't make any sense
      field = if (value?.matches("(IMG|DSC)?[0-9-_.]+".toRegex()) == true) null else value
    }
}
