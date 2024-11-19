package photos

import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.ServletContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocalContentTest {
  val servletContext = mockk<ServletContext>()

  @Test fun `does't fail if no content dir`() {
    every { servletContext.getRealPath("content") } returns null
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("Anything")).isNull()
  }

  @Test fun `loads content from markdown files`() {
    every { servletContext.getRealPath("content") } returns javaClass.getResource("/test_content")!!.path
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("Album")).isEqualTo(AlbumContent("<p>Extra content</p>\n", null))
  }

  @Test fun `loads metadata from markdown files`() {
    every { servletContext.getRealPath("content") } returns javaClass.getResource("/test_content")!!.path
    val content = LocalContent(servletContext)
    assertThat(content.forAlbum("AlbumWithMetadata")).isEqualTo(AlbumContent("<h1>Title</h1>\n", GeoLocation("59 24")))
  }
}
