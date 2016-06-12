package net.azib.photos

import java.text.SimpleDateFormat

open class Entity {
  var title: String? = null
  open var description: String? = null
    get() = field ?: ""

  var thumbUrl: String? = null
  var timestamp: Long? = null
  var geo: GeoLocation? = null

  val timestampISO: String
    get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(timestamp)
}
