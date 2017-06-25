package photos

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek

class PhotoTest: Spek({
  val photo = Photo()

  describe("description") {
    it("is never null for Velocity") {
      assertThat(photo.description).isEmpty()
    }

    it("leaves normal descriptions intact") {
      photo.description = "Hello"
      assertThat(photo.description).isEqualTo("Hello")
    }

    it("removes filename-like descriptions") {
      photo.description = "20130320_133707"
      assertThat(photo.description).isEmpty()
    }
  }
})