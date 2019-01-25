package photos

import util.OAuth
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class Http {
  fun get(url: String): InputStream {
    val conn = connect(url)
    OAuth.authorize(conn)
    if (conn.responseCode != 200)
      throw MissingResourceException(url + ": " + (conn.errorStream
          ?: conn.inputStream).readBytes().toString(Charsets.UTF_8), null, null)
    return conn.inputStream
  }

  internal fun connect(url: String) = URL(url).openConnection() as HttpURLConnection
}