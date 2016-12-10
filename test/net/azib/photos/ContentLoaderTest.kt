package net.azib.photos

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import javax.servlet.ServletContext

class ContentLoaderTest(): Spek({
  val servletContext = mock<ServletContext>()

  it("loads content from markdown files") {
    whenever(servletContext.getRealPath("content")).thenReturn(javaClass.getResource("/test_content").path)
    val loader = ContentLoader(servletContext)
    assertThat(loader.albums).isEqualTo(mapOf("Album" to "<p>Extra content</p>\n"))
  }

  it("does't fail if no content dir") {
    whenever(servletContext.getRealPath("content")).thenReturn(null)
    val loader = ContentLoader(servletContext)
    assertThat(loader.albums).isEmpty()
  }
})
