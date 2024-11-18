package photos

import integration.BaseUrl
import java.time.Instant

open class Entity(var id: String? = null, open var title: String? = null) {
  var baseUrl: BaseUrl? = null
  var timestamp: Long? = null
  var geo: GeoLocation? = null

  var timestampISO: String?
    get() = timestamp?.formatISO()
    set(iso) {
      if (iso != null) Instant.parse(iso).toEpochMilli().also { timestamp = it }
    }

  open val thumbUrlLarge: String?
    get() = baseUrl?.fit(1200, 800)

  companion object {
    private fun Long.formatISO() = Instant.ofEpochMilli(this).toString()
  }
}
