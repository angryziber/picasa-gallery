package integration

import java.util.*

/**
 * AppEngine doesn't support background threads (ThreadManager.createBackgroundThread() fails)
 * So let's collect background tasks here that are executed from cron.xml
 */
object BackgroundTasks {
  private val queue = ArrayDeque<() -> Unit>()

  fun submit(task: () -> Unit) {
    queue.add(task)
  }

  fun run() {
    queue.poll()?.let { it() }
  }
}