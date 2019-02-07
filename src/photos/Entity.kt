package photos

import java.text.SimpleDateFormat
import java.util.*

open class Entity(var id: String? = null, open var title: String? = null, description: String? = null) {
  var thumbUrl: String? = null
  var timestamp: Long? = null
  var geo: GeoLocation? = null

  open var description = description
    get() = field ?: ""

  var timestampISO: String?
    get() = timestamp?.formatISO()
    set(iso) {
      synchronized(timestampFormat) {
        timestamp = timestampFormat.parse(iso).time
      }
    }

  companion object {
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").apply {
      timeZone = TimeZone.getTimeZone("UTC")
    }

    private fun Long.formatISO() = synchronized(timestampFormat) {
      timestampFormat.format(this)
    }
  }
}
