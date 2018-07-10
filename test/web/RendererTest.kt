package web

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse

class RendererTest() : StringSpec({
  val writer = StringWriter()

  "escapes html" {
    val servletContext = mock<ServletContext>()
    whenever(servletContext.getRealPath("/WEB-INF/views")).thenReturn("test")
    val response = mock<HttpServletResponse>()
    whenever(response.writer).thenReturn(PrintWriter(writer))

    Renderer(servletContext).invoke("test", null, mutableMapOf("description" to "\"hello\""), response)

    assertThat(writer.toString()).isEqualTo("<p>&quot;hello&quot;</p>")
  }
})