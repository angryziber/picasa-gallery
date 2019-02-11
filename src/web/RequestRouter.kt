package web

import photos.*
import integration.OAuth
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND

class RequestRouter(
    val req: HttpServletRequest,
    val res: HttpServletResponse,
    val render: Renderer,
    localContent: LocalContent,
    val chain: FilterChain
) {
  companion object {
    val startTime = System.currentTimeMillis() / 1000 % 1000000
  }

  val userAgent: String = req.getHeader("User-Agent") ?: ""
  val path = req.servletPath
  val pathParts = path.substring(1).split("/")
  val host = req.getHeader("host")
  var requestedUser = req["by"]
  val random = req["random"]
  val searchQuery = req["q"]
  val auth = requestedUser?.let { OAuth.auths[it] } ?: OAuth.default
  val picasa = Picasa(auth, if (auth.isDefault) localContent else null)
  var bot = isBot(userAgent) || req["bot"] != null

  fun invoke() {
    try {
      if (req["reload"] != null) Cache.reload()

      when {
        "/oauth" == path -> handleOAuth()
        auth.refreshToken == null -> throw Redirect("/oauth")
        random != null -> renderRandom()
        searchQuery != null -> renderSearch(searchQuery)
        (path == null || "/" == path) && requestedUser == null -> throw Redirect(picasa.urlPrefix)
        picasa.urlPrefix == path || "/" == path -> renderGallery()
        path.isResource() -> chain.doFilter(req, res)
        // pathParts.size == 1 -> throw Redirect(picasa.urlPrefix + path)
        pathParts.size == 2 -> renderPhotoPage(pathParts[0], pathParts[1])
        else -> renderAlbum(pathParts.last())
      }
    }
    catch (e: Redirect) {
      res.status = SC_MOVED_PERMANENTLY
      res.setHeader("Location", e.path)
    }
    catch (e: MissingResourceException) {
      res.sendError(SC_NOT_FOUND, e.message)
    }
  }

  private fun render(template: String, model: Any?, extra: Map<String, Any?> = emptyMap()) {
    val attrs = mutableMapOf(
      "config" to Config,
      "bot" to bot,
      "mobile" to detectMobile(),
      "picasa" to picasa,
      "profile" to auth.profile,
      "host" to host,
      "servletPath" to path,
      "startTime" to startTime
    )
    attrs += extra
    render(template, model, attrs, res)
  }

  private fun String.isResource() = lastIndexOf('.') >= length - 5

  private fun renderGallery() {
    render("gallery", picasa.gallery)
  }

  private fun renderSearch(q: String) {
    val album = picasa.search(q)
    album.title = "Photos matching '$q'"
    render("album", album)
  }

  private fun renderPhotoPage(albumName: String, photoId: String) {
    val album = picasa.gallery[albumName] ?: throw MissingResourceException(path, "", "")
    val photo = picasa.findAlbumPhoto(album, photoId) ?: throw MissingResourceException(path, "", "")
    val redirectUrl = "/$albumName${picasa.urlSuffix}#$photoId"
    render(res) { views.photo(photo, album, auth.profile!!, if (bot) null else redirectUrl) }
  }

  private fun renderAlbum(name: String) {
    val album = picasa.gallery[name] ?: throw MissingResourceException(path, "", "")

    if (album.id == name && album.id != album.name)
      throw Redirect("/${album.name}${picasa.urlSuffix}")

    val pageToken = req["pageToken"]
    val part = if (bot) AlbumPart(picasa.getAlbumPhotos(album), null)
               else picasa.getAlbumPhotos(album, pageToken)
    if (pageToken == null)
      render("album", album, mapOf("albumPart" to part))
    else
      render("albumPart", part, mapOf("album" to album))
  }

  private fun handleOAuth() {
    val code = req["code"] ?: throw Redirect(OAuth.startUrl(host))

    val auth = if (OAuth.default.refreshToken == null) OAuth.default else OAuth(null)
    val token = auth.token(code)

    auth.profile?.slug?.let {
      OAuth.auths[it] = auth
      if (!auth.isDefault) throw Redirect("/?by=$it")
    }
    render("oauth", token)
  }

  private fun renderRandom() {
    val numRandom = if (random.isNotEmpty()) random.toInt() else 1
    val randomPhotos = picasa.getRandomPhotos(numRandom)
    render(res) { views.random(randomPhotos, req["delay"], req["refresh"] != null) }
  }

  private fun detectMobile() =
     userAgent.contains("Mobile") && !userAgent.contains("iPad") && !userAgent.contains("Tab")
  
  internal fun isBot(userAgent: String) =
    userAgent.contains("bot/", true) || userAgent.contains("spider/", true)
}

class Redirect(val path: String): Exception()

operator fun HttpServletRequest.get(param: String) = getParameter(param)
