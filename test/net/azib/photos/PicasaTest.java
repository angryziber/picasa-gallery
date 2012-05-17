package net.azib.photos;

import com.google.gdata.data.photos.AlbumEntry;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class PicasaTest {
  private Picasa picasa = new Picasa(null);

  @Test
  public void weightedRandomDistributesAccordingToTheSizeOfAlbum() throws Exception {
    AlbumEntry album1 = mock(AlbumEntry.class);
    when(album1.getPhotosUsed()).thenReturn(1);
    AlbumEntry album2 = mock(AlbumEntry.class);
    when(album2.getPhotosUsed()).thenReturn(2);
    AlbumEntry album3 = mock(AlbumEntry.class);
    when(album3.getPhotosUsed()).thenReturn(3);

    List<AlbumEntry> albums = asList(album1, album2, album3);
    picasa = spy(picasa);

    doReturn(0).when(picasa).random(6);
    assertSame(album1, picasa.weightedRandom(albums));

    doReturn(1).when(picasa).random(6);
    assertSame(album2, picasa.weightedRandom(albums));

    doReturn(3).when(picasa).random(6);
    assertSame(album3, picasa.weightedRandom(albums));

    doReturn(5).when(picasa).random(6);
    assertSame(album3, picasa.weightedRandom(albums));
  }
}
