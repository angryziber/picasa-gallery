package web

import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse

class RendererTest : StringSpec({
  val out = StringWriter()

  "escapes html" {
    val servletContext = mockk<ServletContext>(relaxed = true) {
      every {getRealPath("/WEB-INF/views")} returns "test"
    }
    val response = mockk<HttpServletResponse>(relaxed = true) {
      every {writer} returns PrintWriter(out)
    }

    Renderer(servletContext).invoke("test", null, mutableMapOf("description" to "\"hello\""), response)

    assertThat(out.toString()).isEqualTo("<p>&quot;hello&quot;</p>")
  }
})