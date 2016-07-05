package net.azib.photos

import com.nhaarman.mockito_kotlin.*
import org.jetbrains.spek.api.Spek
import org.junit.Assert.assertTrue
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RequestRouterTest: Spek({
  val req = mock<HttpServletRequest>()
  val res = mock<HttpServletResponse>()

  beforeEach {
    reset(req, res)
    whenever(req.servletPath).thenReturn("/")
  }

  describe("bots") {
    it("detects") {
      val router = RequestRouter(req, res, mock(), mock())
      assertTrue(router.isBot("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"))
      assertTrue(router.isBot("Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)"))
      assertTrue(router.isBot("Mozilla/5.0 (compatible; AhrefsBot/5.0; +http://ahrefs.com/robot/)"))
      assertTrue(router.isBot("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)"))
      assertTrue(router.isBot("Sogou web spider/4.0(+http://www.sogou.com/docs/help/webmasters.htm#07)"))
    }

    it("redirects to / in case of other user's request") {
      whenever(req.getParameter("by")).thenReturn("other.user")
      whenever(req.getHeader("User-Agent")).thenReturn("Googlebot/2")

      RequestRouter(req, res, mock(), mock()).invoke()

      verify(res).sendRedirect("/")
    }
  }

  describe("album") {
    it("redirects id urls to names") {
      whenever(req.servletPath).thenReturn("/123123123")
      whenever(req.getHeader("User-Agent")).thenReturn("Normal Browser")

      val router = RequestRouter(req, res, mock(), mock())
      router.picasa = spy(router.picasa)
      doReturn(Album(id="123123123", name="Hello")).whenever(router.picasa).getAlbum("123123123")

      router.invoke()

      verify(res).sendRedirect("/Hello")
    }
  }
})
