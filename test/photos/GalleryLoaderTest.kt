package photos

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import util.XMLParser

class GalleryLoaderTest: StringSpec({
  val xml = Gallery::class.java.getResourceAsStream("gallery.xml")

  "parses gallery feed" {
    val gallery = XMLParser(GalleryLoader(LocalContent(null), 212)).parse(xml)
    assertThat(gallery.authorId).isEqualTo("117440562642491680332")
    assertThat(gallery.author).isEqualTo("Anton Keks")
    assertThat(gallery.timestampISO).isEqualTo("2016-05-24T19:13:11Z")
    assertThat(gallery.albums.size).isEqualTo(1)

    val album = gallery.albums.values.first()
    assertThat(album.id).isEqualTo("6212669462372660321")
    assertThat(album.name).isEqualTo("Chernobyl")
    assertThat(album.title).isEqualTo("Chernobyl")
    assertThat(album.description).isEqualTo("Apocalyptic experience in Chernobyl and Pripyat, a soviet city abandoned in 1986 after the nuclear disaster. Current radiation levels are compatible with life :-)")
    assertThat(album.author).isEqualTo("Anton Keks")
    assertThat(album.access).isEqualTo(Album.Access.public)
    assertThat(album.thumbUrl).isEqualTo("https://lh3.googleusercontent.com/-EfV7Xxjk3gk/VjfV9bujtGE/AAAAAAABKUY/gQBUlooE9lsYdyZ1O7ciOiGo-5pch3_DQCHM/s212-c/Chernobyl.jpg")
    assertThat(album.timestampISO).isEqualTo("2015-11-05T21:37:39Z")
    assertThat(album.geo!!.lat).isEqualTo(51.276303f)
    assertThat(album.geo!!.lon).isEqualTo(30.221899f)
    assertThat(album.size()).isEqualTo(159)
  }

  "loads non-public albums which exist in content" {
    val content = mock<LocalContent> {
      on {contains("blah")} doReturn true
    }
    val loader = GalleryLoader(content, 212)

    assertThat(loader.skip(Album(name = "blah"))).isFalse()
    assertThat(loader.skip(Album(name = "other"))).isTrue()
  }
})