package integration

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class Http {
  fun send(auth: OAuth, url: String, body: String? = null): InputStream {
    val conn = connect(url)
    conn.setRequestProperty("Accept", "application/json")
    auth.authorize(conn)

    if (body != null) {
      conn.setRequestProperty("Content-Type", "application/json")
      conn.doOutput = true
      conn.outputStream.bufferedWriter().use {
        it.write(body)
      }
    }

    if (conn.responseCode != 200)
      throw MissingResourceException(url + ": " + (conn.errorStream ?: conn.inputStream)
          .readBytes().toString(Charsets.UTF_8), null, null)

    return conn.inputStream
  }

  internal fun connect(url: String) = URL(url).openConnection() as HttpURLConnection
}