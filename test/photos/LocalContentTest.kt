package photos

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import javax.servlet.ServletContext

class LocalContentTest : Spek({
  val servletContext = mock<ServletContext>()

  it("does't fail if no content dir") {
    whenever(servletContext.getRealPath("content")).thenReturn(null)
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("Anything")).isNull()
  }

  it("loads content from markdown files") {
    whenever(servletContext.getRealPath("content")).thenReturn(javaClass.getResource("/test_content").path)
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("Album")).isEqualTo(AlbumContent("<p>Extra content</p>\n", null))
  }

  it("loads metadata from markdown files") {
    whenever(servletContext.getRealPath("content")).thenReturn(javaClass.getResource("/test_content").path)
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("AlbumWithMetadata")).isEqualTo(AlbumContent("<h1>Title</h1>\n", GeoLocation("59 24")))
  }
})
