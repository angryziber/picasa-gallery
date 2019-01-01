package web

import io.kotlintest.Description
import io.kotlintest.specs.WordSpec
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import photos.Album
import photos.Config
import photos.Photo
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY

class RequestRouterTest: WordSpec() {
  val req = mockk<HttpServletRequest>(relaxed = true)
  val res = mockk<HttpServletResponse>(relaxed = true)

  override fun beforeTest(description: Description) {
    clearMocks(req, res)
    every {req.getParameter(any())} returns null
    every {req.servletPath} returns "/"
  }

  init {
    "bots" should {
      "be detected" {
        val router = router(req, res)
        assertThat(router.isBot("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")).isTrue()
        assertThat(router.isBot("Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)")).isTrue()
        assertThat(router.isBot("Mozilla/5.0 (compatible; AhrefsBot/5.0; +http://ahrefs.com/robot/)")).isTrue()
        assertThat(router.isBot("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)")).isTrue()
        assertThat(router.isBot("Sogou web spider/4.0(+http://www.sogou.com/docs/help/webmasters.htm#07)")).isTrue()
      }

      "redirect to default user in case of other user's request" {
        every {req.getParameter("by")} returns "other.user"
        every {req.getHeader("User-Agent")} returns "Googlebot/2"

        router(req, res).invoke()

        res.verifyRedirectTo("/${Config.defaultUser}")
      }
    }

    "serves photo page for sharing and bots that redirect to album with photo hash" should {
      every {req.getHeader("User-Agent")} returns "Normal Browser"
      every {req.servletPath} returns "/Orlova/5347257660284808946"
      every {req.getParameter("by")} returns "106730404715258343901"

      val render = mockk<Renderer>(relaxed = true)
      val router = router(req, res, render)
      val album = Album(id = "123123123", name = "Orlova")
      val photo = Photo()
      photo.id = "5347257660284808946"
      photo.timestamp = 123L
      album.photos.add(photo)

      router.picasa = spyk(router.picasa) {
        every {getAlbum("Orlova")} returns album
      }

      router.invoke()

      val view = slot<() -> String>()
      verify {render.invoke(res, capture(view))}
      assertThat(view.captured()).contains("'/Orlova?by=106730404715258343901#5347257660284808946'")
    }

    "album" should {
      "redirect id urls to names" {
        every {req.servletPath} returns "/123123123"
        every {req.getHeader("User-Agent")} returns "Normal Browser"

        val router = router(req, res)
        router.picasa = spyk(router.picasa) {
          every {getAlbum("123123123")} returns Album(id = "123123123", name = "Hello")
        }

        router.invoke()

        res.verifyRedirectTo("/Hello")
      }
    }
  }

  private fun router(req: HttpServletRequest, res: HttpServletResponse, render: Renderer = mockk()) = RequestRouter(req, res, render, mockk(), mockk())

  private fun HttpServletResponse.verifyRedirectTo(url: String) {
    verify {status = SC_MOVED_PERMANENTLY}
    verify {setHeader("Location", url)}
  }
}
