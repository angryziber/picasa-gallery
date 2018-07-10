package photos

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import javax.servlet.ServletContext

class LocalContentTest : StringSpec({
  val servletContext = mock<ServletContext>()

  "does't fail if no content dir" {
    whenever(servletContext.getRealPath("content")).thenReturn(null)
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("Anything")).isNull()
  }

  "loads content from markdown files" {
    whenever(servletContext.getRealPath("content")).thenReturn(javaClass.getResource("/test_content").path)
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("Album")).isEqualTo(AlbumContent("<p>Extra content</p>\n", null))
  }

  "loads metadata from markdown files" {
    whenever(servletContext.getRealPath("content")).thenReturn(javaClass.getResource("/test_content").path)
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("AlbumWithMetadata")).isEqualTo(AlbumContent("<h1>Title</h1>\n", GeoLocation("59 24")))
  }
})
