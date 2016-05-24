package net.azib.photos;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GalleryLoaderTest {
  @Test
  public void parse() throws Exception {
    Gallery gallery = new XMLParser<>(new GalleryLoader()).parse(getClass().getResourceAsStream("gallery.xml"));
    assertThat(gallery.author, is("Anton Keks"));
    assertThat(gallery.timestamp, is(1464106391641L));
    assertThat(gallery.albums.size(), is(1));

    Album album = gallery.albums.get(0);
    assertThat(album.name, is("Chernobyl"));
    assertThat(album.title, is("Chernobyl"));
    assertThat(album.description, is("Apocalyptic experience in Chernobyl and Pripyat, a soviet city abandoned in 1986 after the nuclear disaster. Current radiation levels are compatible with life :-)"));
    assertThat(album.author, is("Anton Keks"));
    assertThat(album.isPublic, is(true));
    assertThat(album.thumbUrl, is("https://lh3.googleusercontent.com/-EfV7Xxjk3gk/VjfV9bujtGE/AAAAAAABKUY/gQBUlooE9lsYdyZ1O7ciOiGo-5pch3_DQCHM/s212-c/Chernobyl"));
    assertThat(album.timestamp, is(1432450800000L));
    assertThat(album.geo.getLat(), is(51.276303f));
    assertThat(album.geo.getLon(), is(30.221899f));
    assertThat(album.size(), is(159));
  }
}