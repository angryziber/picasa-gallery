package net.azib.photos

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.jetbrains.spek.api.Spek
import javax.servlet.ServletContext

class ContentLoaderTest(): Spek({
  val servletContext = mock<ServletContext>()

  it("loads content from markdown files") {
    whenever(servletContext.getRealPath("/WEB-INF/content")).thenReturn(javaClass.getResource("/test_content").path)
    val loader = ContentLoader(servletContext)
    assertThat(loader.albums, equalTo(mapOf("Album" to "<p>Extra content</p>\n")))
  }

  it("does't fail if no content dir") {
    whenever(servletContext.getRealPath("/WEB-INF/content")).thenReturn(null)
    val loader = ContentLoader(servletContext)
    assertThat(loader.albums, equalTo(emptyMap()))
  }
})
