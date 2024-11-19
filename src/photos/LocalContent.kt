package photos

import jakarta.servlet.ServletContext
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.File

class LocalContent(path: String?) {
  constructor(servletContext: ServletContext): this(servletContext.getRealPath("content"))

  private val mdParser = Parser.builder().build()
  private val mdRenderer = HtmlRenderer.builder().build()
  private val albums: Map<String, AlbumContent> = path?.let { loadFilesFrom(it) } ?: emptyMap()

  private fun loadFilesFrom(path: String) = File(path).listFiles { file -> file.name.endsWith(".md") }
      ?.associate { file -> file.name.substringBefore('.') to loadContentFrom(file) }

  private fun loadContentFrom(file: File): AlbumContent {
    var source = file.readText().trim()
    val coords = if (source.startsWith(".coords")) {
      val parts = source.split("\n", limit = 2)
      source = parts[1]
      GeoLocation(parts[0].substringAfter(".coords "))
    } else null
    return AlbumContent(markdown2Html(source), coords)
  }

  private fun markdown2Html(source: String): String {
    val document = mdParser.parse(source)
    return mdRenderer.render(document)
  }

  fun contains(albumName: String?) = albums.contains(albumName)
  fun forAlbum(albumName: String?) = albums[albumName]
}

data class AlbumContent(val content: String?, val geo: GeoLocation?)
