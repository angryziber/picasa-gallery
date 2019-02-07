package photos

import integration.Profile
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.assertj.core.api.Assertions.assertThat
import java.util.Arrays.asList

class PicasaTest: StringSpec({
  var picasa = Picasa(Profile(), LocalContent(null))

  "distributes weighted random according to the size of album" {
    val album1 = mockk<Album> {
      every {size()} returns 10
    }
    val album2 = mockk<Album> {
      every {size()} returns 20
    }
    val album3 = mockk<Album> {
      every {size()} returns 30
    }

    val albums = asList(album1, album2, album3)
    picasa = spyk(picasa)

    every {picasa.random(41)} returns 0
    assertThat(picasa.weightedRandom(albums)).isSameAs(album1)

    every {picasa.random(41)} returns 11
    assertThat(picasa.weightedRandom(albums)).isSameAs(album2)

    every {picasa.random(41)} returns 31
    assertThat(picasa.weightedRandom(albums)).isSameAs(album3)

    every {picasa.random(41)} returns 40
    assertThat(picasa.weightedRandom(albums)).isSameAs(album3)
  }
})
