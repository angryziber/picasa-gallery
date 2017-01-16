package net.azib.photos

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY

class RequestRouterTest: Spek({
  val req = mock<HttpServletRequest>()
  val res = mock<HttpServletResponse>()

  beforeEach {
    reset(req, res)
    whenever(req.servletPath).thenReturn("/")
  }

  describe("bots") {
    it("detects") {
      val router = router(req, res)
      assertThat(router.isBot("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")).isTrue()
      assertThat(router.isBot("Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)")).isTrue()
      assertThat(router.isBot("Mozilla/5.0 (compatible; AhrefsBot/5.0; +http://ahrefs.com/robot/)")).isTrue()
      assertThat(router.isBot("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)")).isTrue()
      assertThat(router.isBot("Sogou web spider/4.0(+http://www.sogou.com/docs/help/webmasters.htm#07)")).isTrue()
    }

    it("redirects to default user in case of other user's request") {
      whenever(req.getParameter("by")).thenReturn("other.user")
      whenever(req.getHeader("User-Agent")).thenReturn("Googlebot/2")

      router(req, res).invoke()

      res.verifyRedirectTo("/${Picasa.defaultUser}")
    }
  }

  it("serves shared photo urls that redirect to hashes") {
    whenever(req.getHeader("User-Agent")).thenReturn("Normal Browser")
    whenever(req.servletPath).thenReturn("/Orlova/5347257660284808946")
    whenever(req["by"]).thenReturn("106730404715258343901")

    val render = mock<Renderer>()
    val router = router(req, res, render)
    val album = Album(id = "123123123", name = "Orlova")
    val photo = Photo()
    photo.id = "5347257660284808946"
    album.photos.add(photo)

    router.picasa = spy(router.picasa)
    doReturn(album).whenever(router.picasa).getAlbum("Orlova")

    router.invoke()

    verify(render).invoke("photo", photo, router.attrs, res)
    assertThat(router.attrs["album"]).isEqualTo(album)
    assertThat(router.attrs["redirectUrl"]).isEqualTo("/Orlova?by=106730404715258343901#5347257660284808946")
  }

  describe("album") {
    it("redirects id urls to names") {
      whenever(req.servletPath).thenReturn("/123123123")
      whenever(req.getHeader("User-Agent")).thenReturn("Normal Browser")

      val router = router(req, res)
      router.picasa = spy(router.picasa)
      doReturn(Album(id="123123123", name="Hello")).whenever(router.picasa).getAlbum("123123123")

      router.invoke()

      res.verifyRedirectTo("/Hello")
    }
  }
})

private fun router(req: HttpServletRequest, res: HttpServletResponse, render: Renderer = mock()) = RequestRouter(req, res, render, mock(), mock())

private fun HttpServletResponse.verifyRedirectTo(url: String) {
  verify(this).status = SC_MOVED_PERMANENTLY
  verify(this).setHeader("Location", url)
}
