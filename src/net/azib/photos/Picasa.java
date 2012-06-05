package net.azib.photos;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.IFeed;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.data.photos.UserFeed;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public class Picasa {
    static Properties config = loadConfig();
    static String defaultUser = config.getProperty("google.user");
    static String analytics = config.getProperty("google.analytics");
    static PicasawebService service = new PicasawebService(defaultUser);
    String user = defaultUser;

    public Picasa(String user) {
        if (user != null) this.user = user;
    }

    public String getUser() {
        return user;
    }

    public String getUrlSuffix() {
        return !user.equals(defaultUser) ? "?by=" + user : "";
    }

    public String getAnalytics() {
        return analytics;
    }

    public UserFeed getGallery() {
        return feed("?kind=album&thumbsize=212c", UserFeed.class);
    }

    public AlbumFeed getAlbum(String name) {
        try {
            return feed("/album/" + name + "?imgmax=1024&thumbsize=144c", AlbumFeed.class);
        }
        catch (RuntimeException e) {
            AlbumFeed results = search(name);
            results.setTitle(new PlainTextConstruct("Photos matching '" + name + "'"));
            return results;
        }
    }

    public RandomPhoto getRandomPhoto() {
        List<AlbumEntry> albums = getGallery().getAlbumEntries();
        AlbumEntry album = weightedRandom(albums);
        List<GphotoEntry> photos = feed("/album/" + album.getName() + "?kind=photo&imgmax=1600&max-results=1000&fields=entry(content)", AlbumFeed.class).getEntries();
        return new RandomPhoto(photos.get(random(photos.size())), album.getNickname(), album.getTitle().getPlainText());
    }

    AlbumEntry weightedRandom(List<AlbumEntry> albums) {
        int sum = 0;
        for (AlbumEntry album : albums) sum += album.getPhotosUsed();
        int index = random(sum);

        sum = 0;
        for (AlbumEntry album : albums) {
            sum += album.getPhotosUsed();
            if (sum > index) return album;
        }
        return albums.get(0);
    }

    int random(int max) {
        return (int)(Math.random() * max);
    }

    public AlbumFeed search(String query) {
        return feed("?kind=photo&q=" + query + "&imgmax=1024&thumbsize=144c", AlbumFeed.class);
    }

    private <T extends IFeed> T feed(String url, Class<T> type) {
        try {
            return service.getFeed(new URL("http://picasaweb.google.com/data/feed/api/user/" + getUser() + url), type);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static Properties loadConfig() {
        Properties config = new Properties();
        try {
            config.load(Picasa.class.getResourceAsStream("/config.properties"));
        }
        catch (IOException e) {
            throw new RuntimeException("Can't load config.properties");
        }
        return config;
    }

    public class RandomPhoto {
        private final GphotoEntry photo;
        private final String nickname;
        private final String album;

        public RandomPhoto(GphotoEntry photo, String nickname, String album) {
            this.photo = photo;
            this.nickname = nickname;
            this.album = album;
        }

        public GphotoEntry getPhoto() {
            return photo;
        }

        public String getNickname() {
            return nickname;
        }

        public String getAlbum() {
            return album;
        }
    }
}
