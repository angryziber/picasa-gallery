package net.azib.photos;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.IFeed;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.photos.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.synchronizedMap;

public class Picasa {
    static Properties config = loadConfig();
    static String defaultUser = config.getProperty("google.user");
    static String analytics = config.getProperty("google.analytics");
    static PicasawebService service = new PicasawebService(defaultUser);
    static Random random = new Random(System.nanoTime() / Runtime.getRuntime().freeMemory());

    static Map<String, IFeed> cache = synchronizedMap(new HashMap<String, IFeed>());
    static Map<String, Long> cacheExpiration = synchronizedMap(new HashMap<String, Long>());
    static final long CACHE_EXPIRATION = 30 * 60 * 1000; // 30 min

    String user = defaultUser;
    String authkey;

    public Picasa() {
    }

    public Picasa(String user, String authkey) {
        if (user != null) this.user = user;
        this.authkey = authkey;
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
        return cachedFeed("?kind=album&thumbsize=212c", UserFeed.class);
    }

    public AlbumFeed getAlbum(String name) {
        try {
            return cachedFeed("/album/" + name + "?imgmax=1600&thumbsize=144c", AlbumFeed.class);
        }
        catch (RuntimeException e) {
            AlbumFeed results = search(name);
            results.setTitle(new PlainTextConstruct("Photos matching '" + name + "'"));
            return results;
        }
    }

    public List<CommentEntry> getAlbumComments(String albumName) {
      return cachedFeed("/album/" + albumName + "?kind=comment", PhotoFeed.class).getCommentEntries();
    }

    public RandomPhoto getRandomPhoto() {
        List<AlbumEntry> albums = getGallery().getAlbumEntries();
        AlbumEntry album = weightedRandom(albums);
        List<GphotoEntry> photos = cachedFeed("/album/" + album.getName() + "?kind=photo&imgmax=1600&max-results=1000&fields=entry(content)", AlbumFeed.class).getEntries();
        return new RandomPhoto(photos.get(random(photos.size())), album.getNickname(), album.getTitle().getPlainText());
    }

    AlbumEntry weightedRandom(List<AlbumEntry> albums) {
        int sum = 0;
        for (AlbumEntry album : albums) sum += album.getPhotosUsed() >> 1;
        int index = random(sum);

        sum = 0;
        for (AlbumEntry album : albums) {
            sum += album.getPhotosUsed() >> 1;
            if (sum > index) return album;
        }
        return albums.get(0);
    }

    int random(int max) {
        return random.nextInt(max);
    }

    public AlbumFeed search(String query) {
        return feed("?kind=photo&q=" + query + "&imgmax=1024&thumbsize=144c", AlbumFeed.class);
    }

    @SuppressWarnings({"unchecked", "SynchronizationOnLocalVariableOrMethodParameter"})
    private <T extends IFeed> T cachedFeed(String url, Class<T> type) {
        final String key = (user + url).intern();
        synchronized (key) {
            Long expiration = cacheExpiration.get(key);
            if (expiration != null && expiration > currentTimeMillis()) {
                return (T)cache.get(key);
            }
            else {
                T feed = feed(url, type);
                cache.put(key, feed);
                cacheExpiration.put(key, currentTimeMillis() + CACHE_EXPIRATION);
                return feed;
            }
        }
    }

    private <T extends IFeed> T feed(String url, Class<T> type) {
        try {
            url = "http://picasaweb.google.com/data/feed/api/user/" + user + url;
            if (authkey != null) url += (url.contains("?") ? "&" : "?") + "authkey=" + authkey;
            return service.getFeed(new URL(url), type);
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
