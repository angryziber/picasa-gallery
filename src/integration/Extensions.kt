package integration

import java.util.logging.Logger

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
