package net.azib.photos

interface XMLListener<T> {
  class StopParse : Exception()

  val result: T

  fun rootElement(name: String) {}

  fun rootElementEnd(name: String) {}

  @Throws(StopParse::class)
  fun value(path: String, value: String)

  @Throws(StopParse::class)
  fun start(path: String) {}

  @Throws(StopParse::class)
  fun end(path: String) {}
}
