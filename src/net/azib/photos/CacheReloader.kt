package net.azib.photos

object CacheReloader {
  fun reload() = Picasa.cache.entries.forEach {
    Picasa.loadAndParse(it.key, it.value.javaClass.newInstance())
  }
}
