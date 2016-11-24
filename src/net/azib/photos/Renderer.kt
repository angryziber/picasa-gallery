package net.azib.photos

import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.util.*
import java.util.logging.Logger
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse

open class Renderer(servletContext: ServletContext) {
  private val logger = Logger.getLogger(RequestRouter::class.java.name)
  private var velocity: VelocityEngine

  init {
    val velocityProps = Properties()
    velocityProps.setProperty("file.resource.loader.path", servletContext.getRealPath("/WEB-INF/views"))
    velocityProps.setProperty("file.resource.loader.cache", "true")
    velocity = VelocityEngine(velocityProps)
    velocity.setApplicationAttribute("javax.servlet.ServletContext", servletContext)
    velocity.init()
  }

  open operator fun invoke(template: String, source: Any?, attrs: MutableMap<String, Any?>, response: HttpServletResponse) {
    val start = System.currentTimeMillis()

    if (response.contentType == null)
      response.contentType = "text/html; charset=utf8"
    if (source is Entity && source.timestamp != null)
      response.addDateHeader("Last-Modified", source.timestamp!!)

    response.setHeader("Cache-Control", "public")

    attrs[template] = source
    val ctx = VelocityContext(attrs)
    val tmpl = velocity.getTemplate(template + ".vm")
    tmpl.merge(ctx, response.writer)

    logger.info("Rendered in " + (System.currentTimeMillis() - start) + " ms")
  }
}