package net.azib.photos

import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object URLLoader {
  private val cache: MutableMap<String, XMLListener<Entity>> = ConcurrentHashMap()

  fun reload() = cache.entries.forEach {
    loadAndParse(it.key, it.value.javaClass.newInstance())
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : Entity> load(url: String, loader: XMLListener<T>): T {
    synchronized (url.intern()) {
      return cache.getOrPut(url) { loadAndParse(url, loader) }.result as T
    }
  }

  private fun <T> loadAndParse(fullUrl: String, loader: XMLListener<T>): XMLListener<T> {
    val conn = URL(fullUrl).openConnection() as HttpURLConnection
    if (conn.responseCode != 200) throw MissingResourceException(fullUrl, null, null)
    conn.inputStream.use {
      XMLParser(loader).parse(it)
      return loader
    }
  }
}
