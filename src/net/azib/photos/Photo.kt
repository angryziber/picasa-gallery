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
}
