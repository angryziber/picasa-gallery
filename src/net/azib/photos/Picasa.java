package net.azib.photos;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.IFeed;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.photos.*;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.min;

public class Picasa {
  static Properties config = loadConfig();
  static String defaultUser = config.getProperty("google.user");
  static String analytics = config.getProperty("google.analytics");
  static PicasawebService service = new PicasawebService(defaultUser);
  static Random random = new SecureRandom();

  static Map<String, IFeed> cache = new ConcurrentHashMap<>();

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

  public UserFeed getGallery() throws IOException, ServiceException {
    String url = "?kind=album&thumbsize=212c";
    url += "&fields=id,updated,gphoto:*,entry(title,summary,updated,content,category,gphoto:*,media:*,georss:*)";
    return cachedFeed(url, UserFeed.class);
  }

  public AlbumFeed getAlbum(String name) throws IOException, ServiceException {
    String url = name.matches("\\d+") ? "/albumid/" + name : "/album/" + urlEncode(name);
    url += "?kind=photo,comment&imgmax=1600&thumbsize=144c";
    url += "&fields=id,updated,title,subtitle,icon,gphoto:*,entry(title,summary,content,author,category,gphoto:id,gphoto:photoid,gphoto:width,gphoto:height,gphoto:commentCount,gphoto:timestamp,exif:*,media:*)";
    return fixPhotoDescriptions(cachedFeed(url, AlbumFeed.class));
  }

  private AlbumFeed fixPhotoDescriptions(AlbumFeed album) {
    for (PhotoEntry photo : album.getPhotoEntries()) {
      // remove filename-like descriptions that don't make any sense
      String desc = photo.getDescription().getPlainText();
      if (desc != null && desc.matches("(IMG|DSC)?[0-9-_.]+")) {
        photo.setDescription(new PlainTextConstruct());
      }
    }
    return album;
  }

  public RandomPhotos getRandomPhotos(int numNext) throws IOException, ServiceException {
    List<AlbumEntry> albums = getGallery().getAlbumEntries();
    AlbumEntry album = weightedRandom(albums);
    List<GphotoEntry> photos = cachedFeed("/album/" + urlEncode(album.getName()) + "?kind=photo&imgmax=1600&max-results=1000&fields=entry(content,summary)", AlbumFeed.class).getEntries();
    int index = random(photos.size());
    return new RandomPhotos(photos.subList(index, min(index + numNext, photos.size())), album.getNickname(), album.getTitle().getPlainText());
  }

  AlbumEntry weightedRandom(List<AlbumEntry> albums) {
    int sum = 0;
    for (AlbumEntry album : albums) sum += transform(album.getPhotosUsed());
    int index = random(sum);

    sum = 0;
    for (AlbumEntry album : albums) {
      sum += transform(album.getPhotosUsed());
      if (sum > index) return album;
    }
    return albums.get(0);
  }

  int transform(double n) {
    return (int) (100.0 * Math.log10(1 + n / 50.0));
  }

  int random(int max) {
    return max == 0 ? 0 : random.nextInt(max);
  }

  public AlbumFeed search(String query) throws IOException, ServiceException {
    return fixPhotoDescriptions(feed("?kind=photo&q=" + urlEncode(query) + "&imgmax=1024&thumbsize=144c", AlbumFeed.class));
  }

  @SuppressWarnings({"unchecked", "SynchronizationOnLocalVariableOrMethodParameter"})
  private <T extends IFeed> T cachedFeed(String url, Class<T> type) throws IOException, ServiceException {
    url = toFullUrl(url).intern();
    synchronized (url) {
      T feed = (T) cache.get(url);
      if (feed == null) {
        feed = load(url, type);
        cache.put(url, feed);
      }
      return feed;
    }
  }

  private <T extends IFeed> T feed(String url, Class<T> type) throws IOException, ServiceException {
    url = toFullUrl(url);
    return load(url, type);
  }

  private String toFullUrl(String url) {
    url = "http://picasaweb.google.com/data/feed/api/user/" + urlEncode(user) + url;
    if (authkey != null) url += (url.contains("?") ? "&" : "?") + "authkey=" + authkey;
    return url;
  }

  static <T extends IFeed> T load(String url, Class<T> type) throws IOException, ServiceException {
    return service.getFeed(new URL(url), type);
  }

  static String urlEncode(String name) {
    try {
      return URLEncoder.encode(name, "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      return name;
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
}
