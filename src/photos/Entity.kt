package photos

import integration.BaseUrl
import java.text.SimpleDateFormat
import java.util.*

open class Entity(var id: String? = null, open var title: String? = null, description: String? = null) {
  var baseUrl: BaseUrl? = null
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

  val thumbUrlLarge: String?
    get() = baseUrl?.fit(1200, 800)

  companion object {
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").apply {
      timeZone = TimeZone.getTimeZone("UTC")
    }

    private fun Long.formatISO() = synchronized(timestampFormat) {
      timestampFormat.format(this)
    }
  }
}
