package net.azib.photos

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.File
import javax.servlet.ServletContext

class ContentLoader(servletContext: ServletContext) {
  val albums: Map<String, String>

  private val mdParser = Parser.builder().build()
  private val mdRenderer = HtmlRenderer.builder().build()

  init {
    val contentPath = servletContext.getRealPath("/WEB-INF/content")
    albums = if (contentPath != null) File(contentPath)
        .listFiles { file -> file.name.endsWith(".md") }
        .map { Pair(it.name.substringBefore('.'), markdown2Html(it.readText())) }
        .toMap()
    else emptyMap()
  }

  private fun markdown2Html(source: String): String {
    val document = mdParser.parse(source)
    return mdRenderer.render(document)
  }
}