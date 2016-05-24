package net.azib.photos;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.IFeed;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.*;
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

  public Gallery getGallery() throws IOException, ServiceException {
    String url = "?kind=album&thumbsize=212c";
    url += "&fields=id,updated,gphoto:*,entry(title,summary,updated,content,category,gphoto:*,media:*,georss:*)";
    return loadAndParse(url, new GDataGalleryListener());
  }

  public Album getAlbum(String name) throws IOException, ServiceException {
    String url = name.matches("\\d+") ? "/albumid/" + name : "/album/" + urlEncode(name);
    url += "?kind=photo,comment&imgmax=1600&thumbsize=144c";
    url += "&fields=id,updated,title,subtitle,icon,gphoto:*,georss:where(gml:Point),entry(title,summary,content,author,category,gphoto:id,gphoto:photoid,gphoto:width,gphoto:height,gphoto:commentCount,gphoto:timestamp,exif:*,media:*,georss:where(gml:Point))";
    return fixPhotoDescriptions(loadAndParse(url, new GDataAlbumListener()));
  }

  private <T> T loadAndParse(String url, XMLListener<T> listener) throws IOException {
    url = toFullUrl(url);
    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
    if (conn.getResponseCode() != 200) throw new MissingResourceException(url, null, null);
    try (InputStream in = conn.getInputStream()) {
      return new XMLParser<>(listener).parse(in);
    }
  }

  private Album fixPhotoDescriptions(Album album) {
    for (Photo photo : album.photos) {
      // remove filename-like descriptions that don't make any sense
      String desc = photo.description;
      if (desc != null && desc.matches("(IMG|DSC)?[0-9-_.]+")) {
        photo.description = null;
      }
    }
    return album;
  }

  public RandomPhotos getRandomPhotos(int numNext) throws IOException, ServiceException {
    List<Album> albums = getGallery().albums;
    Album album = weightedRandom(albums);
    List<Photo> photos = fixPhotoDescriptions(loadAndParse("/album/" + urlEncode(album.name) + "?kind=photo&imgmax=1600&max-results=1000&fields=entry(category,content,summary)", new GDataAlbumListener())).photos;
    int index = random(photos.size());
    return new RandomPhotos(photos.subList(index, min(index + numNext, photos.size())), album.author, album.title);
  }

  Album weightedRandom(List<Album> albums) {
    int sum = 0;
    for (Album album : albums) sum += transform(album.size());
    int index = random(sum);

    sum = 0;
    for (Album album : albums) {
      sum += transform(album.size());
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

  public Album search(String query) throws IOException, ServiceException {
    return fixPhotoDescriptions(loadAndParse("?kind=photo&q=" + urlEncode(query) + "&imgmax=1024&thumbsize=144c", new GDataAlbumListener()));
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
