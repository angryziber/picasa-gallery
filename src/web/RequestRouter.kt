package web

import photos.Album
import photos.Config
import photos.LocalContent
import photos.Picasa
import util.OAuth
import util.URLLoader
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND

class RequestRouter(val req: HttpServletRequest, val res: HttpServletResponse, val render: Renderer, content: LocalContent, val chain: FilterChain) {
  companion object {
    val startTime = System.currentTimeMillis() / 1000 % 1000000
  }

  val attrs: MutableMap<String, Any?> = HashMap()
  val userAgent: String? = req.getHeader("User-Agent")
  val path = req.servletPath
  val pathParts = path.substring(1).split("/")
  val host = req.getHeader("host")
  var requestedUser = req["by"]
  val random = req["random"]
  val searchQuery = req["q"]
  var picasa = Picasa(content, requestedUser, req["authkey"])
  var bot = false

  fun invoke() {
    try {
      detectMobile()
      detectBot()

      attrs["config"] = Config
      attrs["picasa"] = picasa
      attrs["host"] = host
      attrs["servletPath"] = path
      attrs["startTime"] = startTime

      if (req["reload"] != null) URLLoader.reload(picasa)

      when {
        "/oauth" == path -> handleOAuth()
        random != null -> renderRandom()
        searchQuery != null -> renderSearch(searchQuery)
        (path == null || "/" == path) && requestedUser == null -> throw Redirect(picasa.urlPrefix)
        picasa.urlPrefix == path || "/" == path -> renderGallery()
        path.isResource() -> chain.doFilter(req, res)
        // pathParts.size == 1 -> throw Redirect(picasa.urlPrefix + path)
        pathParts.size == 2 && pathParts[1].matches("\\d+".toRegex()) -> photoShareUrl(pathParts[0], pathParts[1])
        else -> renderAlbum(pathParts.last())
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

  fun String.isResource() = lastIndexOf('.') >= length - 5

  private fun renderGallery() {
    render("gallery", picasa.gallery, attrs, res)
  }

  private fun renderSearch(q: String) {
    // TODO: no longer works for non-logged-in requests
    val album = picasa.search(q)
    album.title = "Photos matching '$q'"
    render("album", album, attrs, res)
  }

  private fun photoShareUrl(albumId: String, photoId: String) {
    if (bot) throw MissingResourceException(path, "", "")
    val album = picasa.getAlbum(albumId)
    val photo = album.photos.find { it.id == photoId } ?: throw MissingResourceException(path, "", "")
    attrs["redirectUrl"] = "/${albumId}${picasa.urlSuffix}#${photoId}"
    attrs["album"] = album
    render("photo", photo, attrs, res)
  }

  private fun renderAlbum(name: String) {
    var album: Album
    try {
      album = picasa.getAlbum(name)
      if (album.id == name && album.id != album.name)
        throw Redirect("/${album.name}${picasa.urlSuffix}")
    }
    catch (e: MissingResourceException) {
      album = Album(title = pathParts[0], description = "No such album", author = picasa.gallery.author)
      res.status = SC_NOT_FOUND
    }

    render("album", album, attrs, res)
  }

  private fun handleOAuth() {
    val token = req["code"]?.let { code -> OAuth.token(code) }
    render("oauth", token, attrs, res)
  }

  private fun renderRandom() {
    attrs["delay"] = req["delay"]
    attrs["refresh"] = req["refresh"] != null
    val numRandom = if (random.isNotEmpty()) random.toInt() else 1
    render("random", picasa.getRandomPhotos(numRandom), attrs, res)
  }

  private fun detectMobile() {
    attrs["mobile"] = userAgent != null && userAgent.contains("Mobile") && !userAgent.contains("iPad") && !userAgent.contains("Tab")
  }

  private fun detectBot() {
    bot = isBot(userAgent)
    if (bot && requestedUser != null) {
      throw Redirect("/${Config.defaultUser}")
    }
    attrs["bot"] = bot
  }

  internal fun isBot(userAgent: String?): Boolean {
    return userAgent == null || userAgent.contains("bot/", true) || userAgent.contains("spider/", true)
  }
}

class Redirect(val path: String): Exception()

operator fun HttpServletRequest.get(param: String) = getParameter(param)
