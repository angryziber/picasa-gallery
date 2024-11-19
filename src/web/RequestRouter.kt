package web

import integration.BackgroundTasks
import integration.OAuth
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY
import jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND
import photos.AlbumPart
import photos.Cache
import photos.Picasa
import java.util.*

class RequestRouter(
  val req: HttpServletRequest,
  val res: HttpServletResponse,
  val chain: FilterChain,
  val render: Renderer,
  var requestedUser: String? = req["by"],
  val auth: OAuth = requestedUser?.let { OAuth.auths[it] } ?: OAuth.default,
  val picasa: Picasa = Picasa.of(auth)
) {
  val userAgent: String = req.getHeader("User-Agent") ?: ""
  val path = req.servletPath
  val pathParts = path.substring(1).split("/")
  val random = req["random"]
  val bot = isBot(userAgent) || req["bot"] != null
  val reqProps = RequestProps(picasa.urlPrefix, picasa.urlSuffix, req.getHeader("host"), bot, detectMobile())

  fun invoke() {
    try {
      if (req["clear"] != null) Cache.clear()
      if (req["reload"] != null) Cache.reload()

      when {
        "/poll" == path || "/_ah/start" == path -> BackgroundTasks.run().also { res.writer.use { it.write("OK") } }
        "/oauth" == path -> handleOAuth()
        auth.refreshToken == null -> throw Redirect("/oauth")
        random != null -> renderRandom()
        (path == null || "/" == path) && requestedUser == null -> throw Redirect(picasa.urlPrefix)
        picasa.urlPrefix == path || "/" == path -> renderGallery()
        pathParts.size == 1 && path.endsWith(".jpg") -> renderAlbumThumb(pathParts.last().substringBefore(".jpg"))
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

  private fun String.isResource() = lastIndexOf('.') >= length - 5

  private fun renderGallery() {
    render(res, picasa.gallery.loadedAt.time) { views.gallery(reqProps, picasa.gallery, auth.profile!!) }
  }

  private fun renderPhotoPage(albumName: String, photoIdxOrId: String) {
    val album = picasa.gallery[albumName] ?: throw Redirect("/")
    val photo = picasa.findAlbumPhoto(album, photoIdxOrId) ?: throw Redirect(album.url)
    val redirectUrl = "/$albumName${picasa.urlSuffix}#$photoIdxOrId"
    render(res) { views.photo(photo, album, auth.profile!!, if (bot) null else redirectUrl) }
  }

  private fun renderAlbum(name: String) {
    val album = picasa.gallery[name] ?: throw Redirect("/")

    if (album.id == name && album.id != album.name)
      throw Redirect("${album.url}${picasa.urlSuffix}")

    val pageToken = req["pageToken"]
    val part = if (bot) AlbumPart(picasa.getAlbumPhotos(album), null)
               else picasa.getAlbumPhotos(album, pageToken)
    if (pageToken == null)
      render(res, album.timestamp) { views.album(album, part, auth.profile!!, reqProps) }
    else
      render(res) { views.albumPart(part, album, reqProps) }
  }

  private fun renderAlbumThumb(name: String) {
    val album = picasa.gallery[name] ?: throw MissingResourceException(path, "", "")
    val x2 = req["x2"] != null
    val thumbContent = if (x2) album.thumbContent2x else album.thumbContent
    if (thumbContent == null) res.sendRedirect(album.baseUrl?.crop(album.thumbSize * (if (x2) 2 else 1)))
    else {
      res.contentType = "image/jpeg"
      res.addIntHeader("Content-Length", thumbContent.size)
      res.addDateHeader("Last-Modified", album.timestamp!!)
      res.addHeader("Cache-Control", "public, max-age=" + (14 * 24 * 3600))
      res.outputStream.write(thumbContent)
    }
  }

  private fun handleOAuth() {
    val code = req["code"] ?: throw Redirect(OAuth.startUrl(reqProps.host))

    val auth = if (OAuth.default.refreshToken == null) OAuth.default else OAuth(null)
    val token = auth.token(code)

    auth.profile?.slug?.let {
      OAuth.auths[it] = auth
      if (!auth.isDefault) throw Redirect("/?by=$it")
    }
    render(res) { views.oauth(token.refreshToken) }
  }

  private fun renderRandom() {
    if (req.remoteAddr == "82.131.59.12" && isNight())
      return render(res) { "<h1>Night mode</h1>" }

    val numRandom = if (random.isNotEmpty()) random.toInt() else 1
    val randomPhotos = picasa.getRandomPhotos(numRandom)
    render(res) { views.random(randomPhotos, req["delay"], req["refresh"] != null) }
  }

  private fun isNight(): Boolean {
    val utcHours = Date().let { it.hours + it.timezoneOffset / 60 }
    return utcHours >= 18 || utcHours < 5
  }

  private fun detectMobile() =
     userAgent.contains("Mobile") && !userAgent.contains("iPad") && !userAgent.contains("Tab")
  
  internal fun isBot(userAgent: String) =
    userAgent.contains("bot/", true) || userAgent.contains("spider/", true)
}

class Redirect(val path: String): Exception()

class RequestProps(val urlPrefix: String, val urlSuffix: String, val host: String, val bot: Boolean, val mobile: Boolean)

operator fun HttpServletRequest.get(param: String) = getParameter(param)
