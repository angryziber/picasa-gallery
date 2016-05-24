package net.azib.photos;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class GDataGalleryListener implements XMLListener<Gallery> {
  private Gallery gallery = new Gallery();
  private Album album;

  @Override
  public Gallery getResult() {
    return gallery;
  }

  @Override
  public void rootElement(String name) {
  }

  @Override
  public void rootElementEnd(String name) {
  }

  @Override
  public void value(String path, String value) throws StopParse {
    switch (path) {
      case "nickname": gallery.author = value; break;
      case "updated": gallery.timestamp = parseTimestamp(value); break;

      case "entry/name": album.name = value; break;
      case "entry/title": album.title = value; break;
      case "entry/summary": album.description = value; break;
      case "entry/nickname": album.author = value; break;
      case "entry/access": album.isPublic = "public".equals(value); break;
      case "entry/timestamp": album.timestamp = parseLong(value); break;
      case "entry/group/thumbnail@url": album.thumbUrl = value; break;
      case "entry/where/Point/pos": album.geo = new GeoLocation(value); break;
      case "entry/numphotos": album.size = parseInt(value); break;
    }
  }

  private long parseTimestamp(String value) {
    try {
      return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(value).getTime();
    }
    catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void start(String path) throws StopParse {
    if ("entry".equals(path)) gallery.albums.add(album = new Album());
  }

  @Override
  public void end(String path) throws StopParse {
  }
}
