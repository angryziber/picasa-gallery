package photos

import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import javax.servlet.ServletContext

class LocalContentTest : StringSpec({
  val servletContext = mockk<ServletContext>()

  "does't fail if no content dir" {
    every { servletContext.getRealPath("content") } returns null
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("Anything")).isNull()
  }

  "loads content from markdown files" {
    every { servletContext.getRealPath("content") } returns javaClass.getResource("/test_content").path
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("Album")).isEqualTo(AlbumContent("<p>Extra content</p>\n", null))
  }

  "loads metadata from markdown files" {
    every { servletContext.getRealPath("content") } returns javaClass.getResource("/test_content").path
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("AlbumWithMetadata")).isEqualTo(AlbumContent("<h1>Title</h1>\n", GeoLocation("59 24")))
  }
})
