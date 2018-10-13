package web

import photos.LocalContent
import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebFilter("/*")
class RequestFilter : Filter {
  private lateinit var render: Renderer
  private lateinit var content: LocalContent

  override fun init(config: FilterConfig) {
    this.render = Renderer(config.servletContext)
    this.content = LocalContent(config.servletContext)
  }

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    RequestRouter(request as HttpServletRequest, response as HttpServletResponse, render, content, chain).invoke()
  }

  override fun destroy() { }
}