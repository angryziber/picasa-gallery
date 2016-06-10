package net.azib.photos;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AlbumLoaderTest {
  @Test
  public void parse() throws Exception {
    Album album = new XMLParser<>(new AlbumLoader()).parse(getClass().getResourceAsStream("album.xml"));
    assertThat(album.getName(), is("Morocco"));
    assertThat(album.getTitle(), is("Morocco"));
    assertThat(album.getDescription(), is("Morocco round trip: Fes, Chefchaouen, Casablanca, Marrakech, Merzouga"));
    assertThat(album.getAuthor(), is("Anton Keks"));
    assertThat(album.getAuthorId(), is("117440562642491680332"));
    assertThat(album.getIsPublic(), is(true));
    assertThat(album.getThumbUrl(), is("https://lh3.googleusercontent.com/-ooqeMhFMze0/VtyhMoYRIKE/AAAAAAABNJQ/DruDrN8NmjEw-QT-28mIrn0OPW4sqwXPgCHM/s160-c/Morocco"));
    assertThat(album.getTimestamp(), is(1431327600000L));
    assertThat(album.getGeo().getLat(), is(31.791702f));
    assertThat(album.getGeo().getLon(), is(-7.09262f));
    assertThat(album.getPhotos().size(), is(1));
    assertThat(album.getComments().size(), is(1));

    Photo photo = album.getPhotos().get(0);
    assertThat(photo.getId(), is("6259054830603480962"));
    assertThat(photo.getTitle(), is("20150511_090348"));
    assertThat(photo.getDescription(), is("Satellite dishes of Fes Medina are the only thing that reminds us it is not 1700s anymore"));
    assertThat(photo.getTimestamp(), is(1431324228000L));
    assertThat(photo.getWidth(), is(4000));
    assertThat(photo.getHeight(), is(2514));
    assertThat(photo.getUrl(), is("https://lh3.googleusercontent.com/-zBnTKPsUg38/VtyhNN_QL4I/AAAAAAABMHg/mK1UCQcRPycGbfAzWgHRoGGZCwUBbBcpgCHM/s1600/20150511_090348"));
    assertThat(photo.getThumbUrl(), is("https://lh3.googleusercontent.com/-zBnTKPsUg38/VtyhNN_QL4I/AAAAAAABMHg/mK1UCQcRPycGbfAzWgHRoGGZCwUBbBcpgCHM/s144-c/20150511_090348"));
    assertThat(photo.getExif().camera, is("Canon EOS 5D Mark II"));
    assertThat(photo.getExif().fstop, is(22.0f));
    assertThat(photo.getExif().exposure, is(0.04f));
    assertThat(photo.getExif().focal, is(80.0f));
    assertThat(photo.getExif().iso, is("100"));
    assertThat(photo.getGeo().getLat(), is(31.791702f));
    assertThat(photo.getGeo().getLon(), is(-7.09262f));

    Comment comment = album.getComments().get(0);
    assertThat(comment.author, is("Roman Fjodorov"));
    assertThat(comment.authorId, is("117865951136784338179"));
    assertThat(comment.avatarUrl, is("https://lh3.googleusercontent.com/-0nl2Qc_XT0I/AAAAAAAAAAI/AAAAAAAAAAA/l91Lk4HVZgo/s48-c/117865951136784338179.jpg"));
    assertThat(comment.text, is("Отлично снято :)"));
    assertThat(comment.photoId, is("5464172735550834786"));
  }
}