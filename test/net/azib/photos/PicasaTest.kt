package net.azib.photos

import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.Mockito.*
import java.util.Arrays.asList

class PicasaTest {
  var picasa = Picasa()

  @Test fun weightedRandomDistributesAccordingToTheSizeOfAlbum() {
    val album1 = mock(Album::class.java, "album1")
    `when`(album1.size()).thenReturn(10)
    val album2 = mock(Album::class.java, "album2")
    `when`(album2.size()).thenReturn(20)
    val album3 = mock(Album::class.java, "album3")
    `when`(album3.size()).thenReturn(30)

    val albums = asList(album1, album2, album3)
    picasa = spy(picasa)

    doReturn(0).`when`(picasa).random(41)
    assertSame(album1, picasa.weightedRandom(albums))

    doReturn(11).`when`(picasa).random(41)
    assertSame(album2, picasa.weightedRandom(albums))

    doReturn(31).`when`(picasa).random(41)
    assertSame(album3, picasa.weightedRandom(albums))

    doReturn(40).`when`(picasa).random(41)
    assertSame(album3, picasa.weightedRandom(albums))
  }
}
