package photos

import java.lang.System.currentTimeMillis
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level.SEVERE
import java.util.logging.Logger

object Cache {
  private val logger = Logger.getLogger(javaClass.name)
  private const val expirationMs = 30 * 60 * 1000
  private val data: MutableMap<String, Entry> = ConcurrentHashMap()

  fun <T> get(key: String, loader: () -> T): T {
    var entry = data[key]
    if (entry == null || (currentTimeMillis() - entry.createdAt) > expirationMs) {
      try {
        entry = Entry(loader(), loader)
        data[key] = entry
      } catch (e: Exception) {
        logger.log(SEVERE, "Failed to load", e)
        if (entry?.value == null) throw e
      }
    }
    return entry!!.value as T
  }

  fun reload() {
    data.clear()
  }

  data class Entry(val value: Any?, val loader: () -> Any?, val createdAt: Long = currentTimeMillis())
}
