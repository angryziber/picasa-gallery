package net.azib.photos

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponse.SC_NOT_FOUND

class SiteMapServlet : HttpServlet() {
  override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    resp.writer.use { out ->
      when (req.servletPath) {
        "/robots.txt" -> {
          resp.contentType = "text/plain"
          out.write("Sitemap: http://${req.getHeader("Host")}/sitemap.xml\n" +
              "User-Agent: *\n" +
              "Allow: /\n")
        }
        "/sitemap.xml" -> {
          resp.contentType = "text/xml"
          val picasa = Picasa()
          RequestRouter.render("sitemap", null, mutableMapOf("gallery" to picasa.gallery), resp)
        }
        else -> resp.sendError(SC_NOT_FOUND)
      }
    }
  }
}
