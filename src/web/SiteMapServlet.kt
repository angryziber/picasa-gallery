package web

import integration.OAuth
import jakarta.servlet.ServletConfig
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import photos.LocalContent
import photos.Picasa
import views.sitemap

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
      out.write(sitemap(req.getHeader("Host"), OAuth.default.profile, picasa.gallery))
    }
  }
}
