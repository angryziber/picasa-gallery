package net.azib.photos;

import com.google.gdata.data.photos.AlbumEntry;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class PicasaTest {
  private Picasa picasa = new Picasa();

  @Test
  public void weightedRandomDistributesAccordingToTheSizeOfAlbum() throws Exception {
    AlbumEntry album1 = mock(AlbumEntry.class, "album1");
    when(album1.getPhotosUsed()).thenReturn(10);
    AlbumEntry album2 = mock(AlbumEntry.class, "album2");
    when(album2.getPhotosUsed()).thenReturn(20);
    AlbumEntry album3 = mock(AlbumEntry.class, "album3");
    when(album3.getPhotosUsed()).thenReturn(30);

    List<AlbumEntry> albums = asList(album1, album2, album3);
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
