package photos

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import java.util.Arrays.asList

class PicasaTest: StringSpec({
  var picasa = Picasa(LocalContent(null))

  "distributes weighted random according to the size of album" {
    val album1 = mock<Album>()
    whenever(album1.size()).thenReturn(10)
    val album2 = mock<Album>()
    whenever(album2.size()).thenReturn(20)
    val album3 = mock<Album>()
    whenever(album3.size()).thenReturn(30)

    val albums = asList(album1, album2, album3)
    picasa = spy(picasa)

    doReturn(0).whenever(picasa).random(41)
    assertThat(picasa.weightedRandom(albums)).isSameAs(album1)

    doReturn(11).whenever(picasa).random(41)
    assertThat(picasa.weightedRandom(albums)).isSameAs(album2)

    doReturn(31).whenever(picasa).random(41)
    assertThat(picasa.weightedRandom(albums)).isSameAs(album3)

    doReturn(40).whenever(picasa).random(41)
    assertThat(picasa.weightedRandom(albums)).isSameAs(album3)
  }
})
