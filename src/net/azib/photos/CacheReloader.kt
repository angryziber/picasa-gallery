package net.azib.photos

class CacheReloader {
  fun reload() = Picasa.cache.entries.forEach {
    Picasa.loadAndParse(it.key, it.value)
  }
}
