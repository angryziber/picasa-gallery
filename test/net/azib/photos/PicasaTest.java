package net.azib.photos;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class PicasaTest {
  private Picasa picasa = new Picasa();

  @Test
  public void weightedRandomDistributesAccordingToTheSizeOfAlbum() throws Exception {
    Album album1 = mock(Album.class, "album1");
    when(album1.size()).thenReturn(10);
    Album album2 = mock(Album.class, "album2");
    when(album2.size()).thenReturn(20);
    Album album3 = mock(Album.class, "album3");
    when(album3.size()).thenReturn(30);

    List<Album> albums = asList(album1, album2, album3);
    picasa = spy(picasa);

    doReturn(0).when(picasa).random(41);
    assertSame(album1, picasa.weightedRandom(albums));

    doReturn(11).when(picasa).random(41);
    assertSame(album2, picasa.weightedRandom(albums));

    doReturn(31).when(picasa).random(41);
    assertSame(album3, picasa.weightedRandom(albums));

    doReturn(40).when(picasa).random(41);
    assertSame(album3, picasa.weightedRandom(albums));
  }
}
