package net.azib.photos

import org.junit.Assert.assertEquals
import org.junit.Test

class PhotoTest {
  val photo = Photo()

  @Test
  fun descriptionIsNeverNullForVelocity() {
    assertEquals("", photo.description)
  }

  @Test fun leaveNormalDescriptionsIntact() {
    photo.description = "Hello"
    assertEquals("Hello", photo.description)
  }

  @Test fun removeFilenameLikeDescriptions() {
    photo.description = "20130320_133707"
    assertEquals("", photo.description)
  }
}