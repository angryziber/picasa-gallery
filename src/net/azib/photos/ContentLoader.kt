package net.azib.photos

import java.io.File
import javax.servlet.ServletContext

class ContentLoader(servletContext: ServletContext) {
  val albums: Map<String, String>

  init {
    val contentPath = servletContext.getRealPath("/WEB-INF/content")
    albums = if (contentPath != null) File(contentPath)
        .listFiles { file -> file.name.endsWith(".html") }
        .map { Pair(it.name.substringBefore('.'), it.readText()) }
        .toMap()
    else emptyMap()
  }
}