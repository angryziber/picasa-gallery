package web

import integration.OAuth
import photos.LocalContent
import photos.Picasa
import views.sitemap
import javax.servlet.ServletConfig
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/sitemap.xml")
class SiteMapServlet : HttpServlet() {
  lateinit var content: LocalContent

  override fun init(config: ServletConfig) {
    content = LocalContent(config.servletContext)
  }

  override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    resp.writer.use { out ->
      resp.contentType = "text/xml"
      resp.setHeader("Cache-Control", "public")
      val picasa = Picasa(OAuth.default, content)
      out.write(sitemap(req.getHeader("Host"), picasa.gallery))
    }
  }
}
