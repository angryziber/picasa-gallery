package photos

import java.lang.System.currentTimeMillis
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit.SECONDS
import java.util.logging.Level.SEVERE
import java.util.logging.Logger
import kotlin.system.measureTimeMillis

object Cache {
  private val logger = Logger.getLogger(javaClass.name)
  private const val expirationMs = 30 * 60 * 1000
  private val data: MutableMap<String, Entry> = ConcurrentHashMap()

  fun <T> get(key: String, loader: () -> T): T {
    var entry = data[key]
    if (entry == null || (currentTimeMillis() - entry.loadedAt) >= expirationMs) {
      try {
        entry = Entry(logTime(key, loader), loader)
        data[key] = entry
      } catch (e: Exception) {
        logger.log(SEVERE, "Failed to load", e)
        if (entry?.value == null) throw e
      }
    }
    return entry!!.value as T
  }

  private fun <T> logTime(key: String, loader: () -> T): T {
    var result: T? = null
    val millis = measureTimeMillis { result = loader() }
    logger.info("Loaded $key in $millis ms")
    return result!!
  }

  fun clear() {
    data.clear()
  }

  fun reload() {
    val pool = Executors.newFixedThreadPool(10, threadFactory())
    data.entries.sortedBy { it.value.loadedAt }.forEach { e ->
      pool.execute {
        e.value.value = logTime(e.key, e.value.loader)
        e.value.loadedAt = currentTimeMillis()
      }
    }
    pool.shutdown()
    pool.awaitTermination(30, SECONDS)
  }

  private fun threadFactory(): ThreadFactory = try {
    com.google.appengine.api.ThreadManager.currentRequestThreadFactory()
  }
  catch (e: Exception) {
    ThreadFactory { Thread(it) }
  }

  data class Entry(var value: Any?, val loader: () -> Any?, var loadedAt: Long = currentTimeMillis())
}
