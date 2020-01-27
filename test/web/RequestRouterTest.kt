package web

import io.kotlintest.TestCase
import io.kotlintest.specs.StringSpec
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import photos.Album
import photos.Photo
import photos.Picasa
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY

class RequestRouterTest: StringSpec() {
  val req = mockk<HttpServletRequest>(relaxed = true)
  val res = mockk<HttpServletResponse>(relaxed = true)

  override fun beforeTest(testCase: TestCase) {
    clearMocks(req, res)
    every {req.getParameter(any())} returns null
    every {req.servletPath} returns "/"
  }

  init {
    "bots are detected" {
      val router = RequestRouter(req, res, mockk(relaxed = true), mockk(relaxed = true), picasa = mockk(relaxed = true))
      assertThat(router.isBot("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")).isTrue()
      assertThat(router.isBot("Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)")).isTrue()
      assertThat(router.isBot("Mozilla/5.0 (compatible; AhrefsBot/5.0; +http://ahrefs.com/robot/)")).isTrue()
      assertThat(router.isBot("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)")).isTrue()
      assertThat(router.isBot("Sogou web spider/4.0(+http://www.sogou.com/docs/help/webmasters.htm#07)")).isTrue()
    }

    "redirects to default user in case of root request" {
      every {req.servletPath} returns "/"
      val picasa = mockk<Picasa>(relaxed = true) {
        every {urlPrefix} returns "/user"
      }

      val router = RequestRouter(req, res, mockk(relaxed = true), mockk(relaxed = true), picasa = picasa)
      router.invoke()

      res.verifyRedirectTo("/user")
    }

    "serves photo page for sharing and bots that redirect to album with photo hash" {
      every {req.getHeader("User-Agent")} returns "Normal Browser"
      every {req.servletPath} returns "/Orlova/5347257660284808946"

      val album = Album(id = "123123123", name = "Orlova")
      val photo = Photo().apply {
        id = "5347257660284808946"
        timestamp = 123
      }

      val render = mockk<Renderer>(relaxed = true)
      val picasa = mockk<Picasa>(relaxed = true) {
        every { gallery["Orlova"] } returns album
        every { urlSuffix } returns "?by=106730404715258343901"
        every { findAlbumPhoto(album, "5347257660284808946") } returns photo
      }
      val router = RequestRouter(req, res, mockk(relaxed = true), render, picasa = picasa)

      router.invoke()

      val view = slot<() -> String>()
      verify {render.invoke(res, null, capture(view))}
      assertThat(view.captured()).contains("'/Orlova?by=106730404715258343901#5347257660284808946'")
    }

    "album redirect id urls to names" {
      every {req.servletPath} returns "/123123123"
      every {req.getHeader("User-Agent")} returns "Normal Browser"

      val picasa = mockk<Picasa>(relaxed = true) {
        every { gallery["123123123"] } returns Album(id = "123123123", name = "Hello")
      }

      val router = RequestRouter(req, res, mockk(relaxed = true), mockk(relaxed = true), picasa = picasa)
      router.invoke()

      res.verifyRedirectTo("/Hello")
    }
  }

  private fun HttpServletResponse.verifyRedirectTo(url: String) {
    verify {status = SC_MOVED_PERMANENTLY}
    verify {setHeader("Location", url)}
  }
}
