package net.azib.photos

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek

class GalleryLoaderTest: Spek({
  val xml = Gallery::class.java.getResourceAsStream("gallery.xml")

  it("parses gallery feed") {
    val gallery = XMLParser(GalleryLoader(212)).parse(xml)
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
    assertThat(album.isPublic).isEqualTo(true)
    assertThat(album.thumbUrl).isEqualTo("https://lh3.googleusercontent.com/-EfV7Xxjk3gk/VjfV9bujtGE/AAAAAAABKUY/gQBUlooE9lsYdyZ1O7ciOiGo-5pch3_DQCHM/s212-c/Chernobyl.jpg")
    assertThat(album.timestampISO).isEqualTo("2015-11-05T21:37:39Z")
    assertThat(album.geo!!.lat).isEqualTo(51.276303f)
    assertThat(album.geo!!.lon).isEqualTo(30.221899f)
    assertThat(album.size()).isEqualTo(159)
  }
})