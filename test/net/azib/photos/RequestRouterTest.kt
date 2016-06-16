package net.azib.photos

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.jetbrains.spek.api.Spek
import org.junit.Assert.assertTrue
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RequestRouterTest: Spek({
  val req = mock<HttpServletRequest>()
  val res = mock<HttpServletResponse>()
  whenever(req.servletPath).thenReturn("/")

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
})
