package net.azib.photos

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.jetbrains.spek.api.Spek
import org.junit.Assert.assertSame
import java.util.Arrays.asList

class PicasaTest: Spek({
  var picasa = Picasa()

  it("distributes weighted random according to the size of album") {
    val album1 = mock<Album>()
    whenever(album1.size()).thenReturn(10)
    val album2 = mock<Album>()
    whenever(album2.size()).thenReturn(20)
    val album3 = mock<Album>()
    whenever(album3.size()).thenReturn(30)

    val albums = asList(album1, album2, album3)
    picasa = spy(picasa)

    doReturn(0).whenever(picasa).random(41)
    assertSame(album1, picasa.weightedRandom(albums))

    doReturn(11).whenever(picasa).random(41)
    assertSame(album2, picasa.weightedRandom(albums))

    doReturn(31).whenever(picasa).random(41)
    assertSame(album3, picasa.weightedRandom(albums))

    doReturn(40).whenever(picasa).random(41)
    assertSame(album3, picasa.weightedRandom(albums))
  }
})
