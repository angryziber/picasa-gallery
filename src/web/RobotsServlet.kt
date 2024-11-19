package web

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

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
