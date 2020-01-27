package web

import javax.servlet.http.HttpServletResponse

open class Renderer {
  open operator fun invoke(response: HttpServletResponse, lastModified: Long? = null, html: () -> String) {
    if (response.contentType == null)
      response.contentType = "text/html; charset=utf8"

    response.setHeader("Cache-Control", "public")
    if (lastModified != null) response.setDateHeader("Last-Modified", lastModified)

    response.writer.write(html())
  }
}