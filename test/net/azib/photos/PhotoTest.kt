package net.azib.photos

import org.jetbrains.spek.api.Spek
import org.junit.Assert.assertEquals

class PhotoTest: Spek({
  val photo = Photo()

  describe("description") {
    it("is never null for Velocity") {
      assertEquals("", photo.description)
    }

    it("leaves normal descriptions intact") {
      photo.description = "Hello"
      assertEquals("Hello", photo.description)
    }

    it("removes filename-like descriptions") {
      photo.description = "20130320_133707"
      assertEquals("", photo.description)
    }
  }
})