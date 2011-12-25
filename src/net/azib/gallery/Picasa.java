package net.azib.gallery;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Picasa {
    static String USER = "anton.keks";

    PicasawebService service = new PicasawebService("xxx");

    public List<AlbumEntry> getAlbums() throws IOException, ServiceException {
        UserFeed feed = service.getFeed(new URL("https://picasaweb.google.com/data/feed/api/user/" + USER + "?kind=album"), UserFeed.class);
        return feed.getAlbumEntries();
    }
}
