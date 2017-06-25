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
    conn.setRequestProperty("Authorization", "Bearer ya29.GlsyBJpV2_ijj2Ob1CPS5DELeI7AoxDN9VzPEnRYomU-6YVzBAByx7XL9AKc0t80GmcACiT5W8FR40X_4ejDngCKO1H_fn89b4Z3pdsZuGtb4ZWDp9bh5jisuRDL")
    if (conn.responseCode != 200) throw MissingResourceException(fullUrl, null, null)
    conn.inputStream.use {
      XMLParser(loader).parse(it)
      return loader
    }
  }
}
