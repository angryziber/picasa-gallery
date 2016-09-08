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
  val searchQuery = req["q"]
  var picasa = Picasa(requestedUser, req["authkey"])
  var bot = false

  operator fun invoke() {
    try {
      detectMobile()
      detectBot()

      attrs["picasa"] = picasa
      attrs["host"] = req.getHeader("host")
      attrs["servletPath"] = path

      if (req["reload"] != null) URLLoader.reload()

      when {
        random != null -> renderRandom()
        searchQuery != null -> renderSearch(searchQuery)
        path == null || "/" == path -> throw Redirect("/${picasa.user}")
        "/${picasa.user}" == path -> renderGallery()
        path.isResource() -> chain.doFilter(req, res)
        else -> renderAlbum()
      }
    }
    catch (e: Redirect) {
      res.status = SC_MOVED_PERMANENTLY
      res.setHeader("Location", e.path)
    }
    catch (e: MissingResourceException) {
      res.sendError(SC_NOT_FOUND)
    }
  }

  fun String.isResource() = lastIndexOf('.') >= length - 4

  private fun renderGallery() {
    render("gallery", picasa.gallery, attrs, res)
  }

  private fun renderSearch(q: String) {
    // TODO: no longer works for non-logged-in requests
    val album = picasa.search(q)
    album.title = "Photos matching '$q'"
    render("album", album, attrs, res)
  }

  private fun renderAlbum() {
    if (pathParts.size > 2) {
      val lastSlashPos = path.lastIndexOf('/')
      if (bot) throw MissingResourceException(path, "", "")
      else throw Redirect(path.replaceRange(lastSlashPos, lastSlashPos+1, "#"))
    }

    val album: Album
    try {
      album = picasa.getAlbum(pathParts[1])
      if (album.id == pathParts[1] && album.id != album.name)
        throw Redirect("/${album.name}${picasa.urlSuffix}")
    }
    catch (e: MissingResourceException) {
      album = Album(title=pathParts[1], description="No such album", author=picasa.gallery.author)
      res.status = SC_NOT_FOUND
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
    bot = isBot(userAgent)
    attrs["bot"] = bot
  }

  internal fun isBot(userAgent: String?): Boolean {
    return userAgent == null || userAgent.contains("bot/", true) || userAgent.contains("spider/", true)
  }

  private operator fun HttpServletRequest.get(param: String) = getParameter(param)

  class Redirect(val path: String): Exception()
}
