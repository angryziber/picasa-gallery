package photos

import java.text.SimpleDateFormat

class Photo : Entity() {
  var width: Int? = null
  var height: Int? = null
  var exif = Exif()
  var albumId: String? = null

  val dateTime: String
    get() = SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp)

  val date: String
    get() = SimpleDateFormat("yyyy-MM-dd").format(timestamp)

  val thumbUrl: String?
    get() = baseUrl?.crop(144)

  val url = baseUrl?.fit(1920, 1080)

  override var description: String?
    get() = super.description
    set(value) {
      // remove filename-like descriptions that don't make any sense
      super.description = if (value?.matches("(IMG|DSC)?[0-9-_.]+".toRegex()) ?: false) null else value
    }
}
