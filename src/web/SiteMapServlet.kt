package web

import photos.LocalContent
import photos.Picasa
import views.sitemap
import javax.servlet.ServletConfig
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND

@WebServlet(urlPatterns = ["/robots.txt", "/sitemap.xml"])
class SiteMapServlet : HttpServlet() {
  lateinit var render: Renderer
  lateinit var content: LocalContent

  override fun init(config: ServletConfig) {
    render = Renderer(config.servletContext)
    content = LocalContent(config.servletContext)
  }

  override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    resp.writer.use { out ->
      when (req.servletPath) {
        "/robots.txt" -> {
          resp.contentType = "text/plain"
          out.write("Sitemap: https://${req.getHeader("Host")}/sitemap.xml\n" +
              "User-Agent: *\n" +
              "Allow: /\n")
        }
        "/sitemap.xml" -> {
          resp.contentType = "text/xml"
          resp.setHeader("Cache-Control", "public")
          val picasa = Picasa(content)
          out.write(sitemap(req.getHeader("Host"), picasa.gallery))
        }
        else -> resp.sendError(SC_NOT_FOUND)
      }
    }
  }
}
