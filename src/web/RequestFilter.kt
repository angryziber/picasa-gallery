package web

import integration.OAuth
import photos.LocalContent
import photos.Picasa
import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebFilter("/*")
class RequestFilter : Filter {
  private lateinit var render: Renderer

  override fun init(config: FilterConfig) {
    this.render = Renderer(config.servletContext)
    if (OAuth.default.isInitialized)
      Picasa.loadDefault(OAuth.default, LocalContent(config.servletContext))
  }

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    RequestRouter(request as HttpServletRequest, response as HttpServletResponse, chain, render).invoke()
  }

  override fun destroy() { }
}