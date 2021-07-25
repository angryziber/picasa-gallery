package photos

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PhotoTest {
  val photo = Photo()

  @Nested inner class description {
    @Test fun `leave normal descriptions intact`() {
      photo.description = "Hello"
      assertThat(photo.description).isEqualTo("Hello")
    }

    @Test fun `remove filename-like descriptions`() {
      photo.description = "20130320_133707"
      assertThat(photo.description).isNull()
    }
  }
}
