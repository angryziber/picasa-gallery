package net.azib.photos

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse

class RendererTest() : Spek({
  val writer = StringWriter()

  it("escapes html") {
    val servletContext = mock<ServletContext>()
    whenever(servletContext.getRealPath("/WEB-INF/views")).thenReturn("test")
    val response = mock<HttpServletResponse>()
    whenever(response.writer).thenReturn(PrintWriter(writer))

    Renderer(servletContext).invoke("test", null, mutableMapOf("hello" to "\"world\""), response)

    assertThat(writer.toString()).isEqualTo("<p>&quot;world&quot;</p>")
  }
})