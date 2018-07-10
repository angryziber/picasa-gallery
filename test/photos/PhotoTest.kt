package photos

import io.kotlintest.specs.WordSpec
import org.assertj.core.api.Assertions.assertThat

class PhotoTest: WordSpec({
  val photo = Photo()

  "description" should {
    "never be null for Velocity" {
      assertThat(photo.description).isEmpty()
    }

    "leave normal descriptions intact" {
      photo.description = "Hello"
      assertThat(photo.description).isEqualTo("Hello")
    }

    "remove filename-like descriptions" {
      photo.description = "20130320_133707"
      assertThat(photo.description).isEmpty()
    }
  }
})