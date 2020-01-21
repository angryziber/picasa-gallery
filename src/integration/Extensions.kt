package integration

import com.google.appengine.api.ThreadManager

fun appEngineThread(runnable: () -> Unit) = ThreadManager.createBackgroundThread(runnable).start()
