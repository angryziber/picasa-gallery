package net.azib.photos

import java.lang.Math.min
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Picasa(val user: String = defaultUser, private val authKey: String? = null) {
  val urlSuffix: String
    get() = if (user != defaultUser) "?by=" + user else ""

  val analytics: String?
    get() = config.getProperty("google.analytics")

  val gallery: Gallery
    get() {
      var url = "?kind=album&thumbsize=212c"
      url += "&fields=id,updated,gphoto:*,entry(title,summary,updated,content,category,gphoto:*,media:*,georss:*)"
      return cachedFeed(url, GalleryLoader())
    }

  fun getAlbum(name: String): Album {
    // TODO: it seems Google now returns only 500 results...
    var url = if (name.matches("\\d+".toRegex())) "/albumid/" + name else "/album/" + urlEncode(name)
    url += "?kind=photo,comment&imgmax=1600&thumbsize=144c"
    url += "&fields=id,updated,title,subtitle,icon,gphoto:*,georss:where(gml:Point),entry(title,summary,content,author,category,gphoto:id,gphoto:photoid,gphoto:width,gphoto:height,gphoto:commentCount,gphoto:timestamp,exif:*,media:*,georss:where(gml:Point))"
    return fixPhotoDescriptions(cachedFeed(url, AlbumLoader()))
  }

  private fun fixPhotoDescriptions(album: Album): Album {
    for (photo in album.photos) {
      // remove filename-like descriptions that don't make any sense
      val desc = photo.description
      if (desc != null && desc.matches("(IMG|DSC)?[0-9-_.]+".toRegex())) {
        photo.description = null
      }
    }
    return album
  }

  fun getRandomPhotos(numNext: Int): RandomPhotos {
    val albums = gallery.albums
    val album = weightedRandom(albums)
    val photos = getAlbum(album.name!!).photos
    val index = random(photos.size)
    return RandomPhotos(photos.subList(index, min(index + numNext, photos.size)), album.author!!, album.title!!)
  }

  internal fun weightedRandom(albums: List<Album>): Album {
    var sum = 0
    for (album in albums) sum += transform(album.size().toDouble())
    val index = random(sum)

    sum = 0
    for (album in albums) {
      sum += transform(album.size().toDouble())
      if (sum > index) return album
    }
    return albums[0]
  }

  internal fun transform(n: Double): Int {
    return (100.0 * Math.log10(1 + n / 50.0)).toInt()
  }

  internal fun random(max: Int): Int {
    return if (max == 0) 0 else random.nextInt(max)
  }

  fun search(query: String): Album {
    return fixPhotoDescriptions(cachedFeed("?kind=photo&q=" + urlEncode(query) + "&imgmax=1024&thumbsize=144c", AlbumLoader()))
  }

  private fun <T : Entity> cachedFeed(query: String, loader: XMLListener<T>): T {
    val url = toFullUrl(query).intern()
    synchronized (url) {
      var cached = cache[url] as XMLListener<T>?
      if (cached == null) {
        loadAndParse(url, loader)
        cached = loader
        cache[url] = cached as XMLListener<Any>
      }
      return cached.result
    }
  }

  private fun toFullUrl(query: String): String {
    var url = "http://picasaweb.google.com/data/feed/api/user/" + urlEncode(user) + query
    if (authKey != null) url += (if (url.contains("?")) "&" else "?") + "authkey=" + authKey
    return url
  }

  private fun urlEncode(name: String): String {
    return URLEncoder.encode(name, "UTF-8")
  }

  companion object {
    internal var config = loadConfig()
    internal var defaultUser = config.getProperty("google.user")
    internal var random: Random = SecureRandom()

    internal var cache: MutableMap<String, XMLListener<Any>> = ConcurrentHashMap()

    internal fun <T> loadAndParse(fullUrl: String, loader: XMLListener<T>): T {
      val conn = URL(fullUrl).openConnection() as HttpURLConnection
      if (conn.responseCode != 200) throw MissingResourceException(fullUrl, null, null)
      conn.inputStream.use { return XMLParser(loader).parse(it) }
    }

    private fun loadConfig(): Properties {
      val config = Properties()
      config.load(Picasa::class.java.getResourceAsStream("/config.properties"))
      return config
    }
  }
}
