package web

import integration.OAuth
import jakarta.servlet.*
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import photos.LocalContent
import photos.Picasa

@WebFilter("/*")
class RequestFilter : Filter {
  private lateinit var render: Renderer

  override fun init(config: FilterConfig) {
    this.render = Renderer()
    if (OAuth.default.isInitialized)
      Picasa.loadDefault(OAuth.default, LocalContent(config.servletContext))
  }

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    RequestRouter(request as HttpServletRequest, response as HttpServletResponse, chain, render).invoke()
  }

  override fun destroy() { }
}
