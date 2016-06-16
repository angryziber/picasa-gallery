package net.azib.photos

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RequestFilter : Filter {
  private lateinit var render: Renderer

  override fun init(config: FilterConfig) {
    this.render = Renderer(config.servletContext)
  }

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    RequestRouter(request as HttpServletRequest, response as HttpServletResponse, chain, render).invoke()
  }

  override fun destroy() { }
}