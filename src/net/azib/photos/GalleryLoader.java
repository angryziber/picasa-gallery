package net.azib.photos;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class GalleryLoader implements XMLListener<Gallery> {
  private Gallery gallery;
  private Album album;

  @Override
  public Gallery getResult() {
    return gallery;
  }

  @Override
  public void rootElement(String name) {
    gallery = new Gallery();
  }

  @Override
  public void rootElementEnd(String name) {
  }

  @Override
  public void value(String path, String value) throws StopParse {
    switch (path) {
      case "nickname": gallery.setAuthor(value); break;
      case "updated": gallery.setTimestamp(parseTimestamp(value)); break;

      case "entry/name": album.setName(value); break;
      case "entry/title": album.setTitle(value); break;
      case "entry/summary": album.setDescription(value); break;
      case "entry/nickname": album.setAuthor(value); break;
      case "entry/access": album.setPublic("public".equals(value)); break;
      case "entry/timestamp": album.setTimestamp(parseLong(value)); break;
      case "entry/group/thumbnail@url": album.setThumbUrl(value); break;
      case "entry/where/Point/pos": album.setGeo(new GeoLocation(value)); break;
      case "entry/numphotos": album.setSize(parseInt(value)); break;
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
    if ("entry".equals(path)) gallery.getAlbums().add(album = new Album());
  }

  @Override
  public void end(String path) throws StopParse {
  }
}
