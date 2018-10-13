package web

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/robots.txt")
class RobotsServlet : HttpServlet() {
  override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    resp.writer.use { out ->
      resp.contentType = "text/plain"
      out.write("Sitemap: https://${req.getHeader("Host")}/sitemap.xml\n" +
          "User-Agent: *\n" +
          "Allow: /\n")
    }
  }
}
