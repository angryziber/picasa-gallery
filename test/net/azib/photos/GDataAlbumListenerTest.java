package net.azib.photos;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GDataAlbumListenerTest {
  @Test
  public void parse() throws Exception {
    Album album = new XMLParser<>(new GDataAlbumListener()).parse(getClass().getResourceAsStream("album.xml"));
    assertThat(album.name, is("Morocco"));
    assertThat(album.title, is("Morocco"));
    assertThat(album.description, is("Morocco round trip: Fes, Chefchaouen, Casablanca, Marrakech, Merzouga"));
    assertThat(album.author, is("Anton Keks"));
    assertThat(album.authorId, is("117440562642491680332"));
    assertThat(album.isPublic, is(true));
    assertThat(album.thumbUrl, is("https://lh3.googleusercontent.com/-ooqeMhFMze0/VtyhMoYRIKE/AAAAAAABNJQ/DruDrN8NmjEw-QT-28mIrn0OPW4sqwXPgCHM/s160-c/Morocco"));
    assertThat(album.timestamp, is(1431327600000L));
    assertThat(album.geo.getLat(), is(31.791702f));
    assertThat(album.geo.getLon(), is(-7.09262f));
    assertThat(album.photos.size(), is(1));
    assertThat(album.comments.size(), is(1));

    Photo photo = album.photos.get(0);
    assertThat(photo.id, is("6259054830603480962"));
    assertThat(photo.title, is("20150511_090348"));
    assertThat(photo.description, is("Satellite dishes of Fes Medina are the only thing that reminds us it is not 1700s anymore"));
    assertThat(photo.timestamp, is(1431324228000L));
    assertThat(photo.width, is(4000));
    assertThat(photo.height, is(2514));
    assertThat(photo.url, is("https://lh3.googleusercontent.com/-zBnTKPsUg38/VtyhNN_QL4I/AAAAAAABMHg/mK1UCQcRPycGbfAzWgHRoGGZCwUBbBcpgCHM/s1600/20150511_090348"));
    assertThat(photo.thumbUrl, is("https://lh3.googleusercontent.com/-zBnTKPsUg38/VtyhNN_QL4I/AAAAAAABMHg/mK1UCQcRPycGbfAzWgHRoGGZCwUBbBcpgCHM/s144-c/20150511_090348"));
    assertThat(photo.exif.camera, is("Canon EOS 5D Mark II"));
    assertThat(photo.exif.fstop, is(22.0f));
    assertThat(photo.exif.exposure, is(0.04f));
    assertThat(photo.exif.focal, is(80.0f));
    assertThat(photo.exif.iso, is("100"));
    assertThat(photo.geo.getLat(), is(31.791702f));
    assertThat(photo.geo.getLon(), is(-7.09262f));

    Comment comment = album.comments.get(0);
    assertThat(comment.author, is("Roman Fjodorov"));
    assertThat(comment.authorId, is("117865951136784338179"));
    assertThat(comment.avatarUrl, is("https://lh3.googleusercontent.com/-0nl2Qc_XT0I/AAAAAAAAAAI/AAAAAAAAAAA/l91Lk4HVZgo/s48-c/117865951136784338179.jpg"));
    assertThat(comment.text, is("Отлично снято :)"));
    assertThat(comment.photoId, is("5464172735550834786"));
  }
}