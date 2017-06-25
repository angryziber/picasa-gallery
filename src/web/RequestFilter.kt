package web

import photos.ContentLoader
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RequestFilter : Filter {
  private lateinit var render: Renderer
  private lateinit var contentLoader: ContentLoader

  override fun init(config: FilterConfig) {
    this.render = Renderer(config.servletContext)
    this.contentLoader = ContentLoader(config.servletContext)
  }

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    RequestRouter(request as HttpServletRequest, response as HttpServletResponse, render, contentLoader, chain).invoke()
  }

  override fun destroy() { }
}