package net.azib.gallery;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.IFeed;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.ServiceException;
import sun.java2d.SunGraphicsEnvironment.T1Filter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Picasa {
    static String USER = "anton.keks";

    PicasawebService service = new PicasawebService("xxx");

    public String getTitle() {
        return "Albums";
    }

    public List<AlbumEntry> getAlbums() {
        UserFeed feed = feed("http://picasaweb.google.com/data/feed/api/user/" + USER + "?kind=album", UserFeed.class);
        return feed.getAlbumEntries();
    }

    public AlbumFeed getAlbum(String name) {
        return feed("http://picasaweb.google.com/data/feed/api/user/" + USER + "/album/" + name + "?imgmax=1024&thumbsize=144c", AlbumFeed.class);
    }

    private <T extends IFeed> T feed(String url, Class<T> type) {
        try {
            return service.getFeed(new URL(url), type);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
