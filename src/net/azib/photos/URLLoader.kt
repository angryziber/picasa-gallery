package net.azib.photos

import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object URLLoader {
  private val cache: MutableMap<String, XMLListener<Entity>> = ConcurrentHashMap()

  fun reload(picasa: Picasa) {
    cache.clear()
    picasa.gallery.albums.forEach { picasa.getAlbum(it.key) }
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : Entity> load(url: String, loader: XMLListener<T>): T {
    synchronized (url.intern()) {
      return cache.getOrPut(url) { loadAndParse(url, loader) }.result as T
    }
  }

  private fun <T> loadAndParse(fullUrl: String, loader: XMLListener<T>): XMLListener<T> {
    val conn = URL(fullUrl).openConnection() as HttpURLConnection
    OAuth.authorize(conn)
    if (conn.responseCode != 200) throw MissingResourceException(fullUrl + ": " + conn.errorStream.readBytes().toString(Charsets.UTF_8), null, null)
    conn.inputStream.use {
      XMLParser(loader).parse(it)
      return loader
    }
  }
}
