package integration

import com.google.appengine.api.ThreadManager
import java.util.logging.Logger

fun appEngineThread(runnable: () -> Unit) = ThreadManager.createBackgroundThread(runnable).start()

private val timeLogger = Logger.getLogger("time")
fun <T> logTime(what: String, block: () -> T): T {
  val start = System.nanoTime()
  try {
    return block()
  }
  finally {
    timeLogger.info("$what in ${(System.nanoTime() - start) / 1000_000}")
  }
}
