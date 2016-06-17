package net.azib.photos

import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND

class RequestRouter(val req: HttpServletRequest, val res: HttpServletResponse, val render: Renderer, val chain: FilterChain) {
  val attrs: MutableMap<String, Any?> = HashMap()
  val userAgent: String? = req.getHeader("User-Agent")
  val path = req.servletPath
  val pathParts = path.split("/")
  var requestedUser = req["by"]
  val random = req["random"]
  val picasa = Picasa(requestedUser, req["authkey"])

  operator fun invoke() {
    try {
      detectMobile()
      detectBot()

      attrs["picasa"] = picasa
      attrs["host"] = req.getHeader("host")
      attrs["servletPath"] = path

      req["reload"] ?: URLLoader.reload()

      when {
        random != null -> renderRandom()
        path == null || "/" == path -> render("gallery", picasa.gallery, attrs, res)
        path.isResource() -> chain.doFilter(req, res)
        else -> renderAlbumOrSearch()
      }
    }
    catch (e: Redirect) {
      res.sendRedirect(e.path)
      res.status = SC_MOVED_PERMANENTLY
    }
    catch (e: MissingResourceException) {
      res.sendError(SC_NOT_FOUND)
    }
  }

  fun String.isResource() = lastIndexOf('.') >= length - 4

  private fun renderAlbumOrSearch() {
    val album: Album
    try {
      album = picasa.getAlbum(pathParts[1])
    }
    catch (e: MissingResourceException) {
      album = picasa.search(pathParts[1])
      album.title = "Photos matching '" + pathParts[1] + "'"
      // TODO: no longer works for non-logged-in requests
    }

    if (pathParts.size > 2) {
      for (photo in album.photos) {
        if (photo.id == pathParts[2]) {
          attrs["photo"] = photo
          break
        }
      }
    }
    render("album", album, attrs, res)
  }

  private fun renderRandom() {
    attrs["delay"] = req["delay"]
    attrs["refresh"] = req["refresh"] != null
    val numRandom = (if (random.isNotEmpty()) random else "1").toInt()
    render("random", picasa.getRandomPhotos(numRandom), attrs, res)
  }

  private fun detectMobile() {
    attrs["mobile"] = userAgent != null && userAgent.contains("Mobile") && !userAgent.contains("iPad") && !userAgent.contains("Tab")
  }

  private fun detectBot() {
    val bot = isBot(userAgent)
    if (bot && (requestedUser != null || random != null)) {
      throw Redirect("/")
    }
    attrs["bot"] = bot
  }

  internal fun isBot(userAgent: String?): Boolean {
    return userAgent == null || userAgent.contains("bot/", true) || userAgent.contains("spider/", true)
  }

  private operator fun HttpServletRequest.get(param: String) = getParameter(param)

  class Redirect(val path: String): Exception()
}
