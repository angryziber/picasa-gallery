package photos

import java.lang.System.currentTimeMillis
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.logging.Level.SEVERE
import java.util.logging.Logger

object Cache {
  private val logger = Logger.getLogger(javaClass.name)
  private const val expirationMs = 55 * 60 * 1000
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
    val pool = Executors.newFixedThreadPool(10, threadFactory())
    data.entries.sortedBy { it.value.createdAt }.forEach { e ->
      pool.execute {
        println("Reloading ${e.key}")
        e.value.value = e.value.loader()
        println("Reloaded ${e.key}")
      }
    }
    pool.shutdown()
    pool.awaitTermination(30, TimeUnit.SECONDS)
  }

  private fun threadFactory(): ThreadFactory = try {
    com.google.appengine.api.ThreadManager.currentRequestThreadFactory()
  }
  catch (e: Exception) {
    ThreadFactory { Thread(it) }
  }

  data class Entry(var value: Any?, val loader: () -> Any?, val createdAt: Long = currentTimeMillis())
}
