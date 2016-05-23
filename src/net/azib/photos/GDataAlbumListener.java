package net.azib.photos;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class GDataAlbumListener implements XMLListener<Album> {
  private Album album = new Album();
  private Photo photo = new Photo();

  @Override public Album getResult() {
    return album;
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
      case "title": album.title = value; break;
      case "subtitle": album.description = value; break;
      case "icon": album.iconUrl = value; break;
      case "timestamp": album.timestamp = parseLong(value); break;
      case "numphotos": album.photos = new ArrayList<>(parseInt(value)); break;

      case "entry/id": photo.id = value; break;
      case "entry/title": photo.title = value; break;
      case "entry/summary": photo.description = value; break;
      case "entry/timestamp": photo.timestamp = parseLong(value); break;
      case "entry/width": photo.width = parseInt(value); break;
      case "entry/height": photo.height = parseInt(value); break;
      case "entry/group/content@url": photo.url = value; break;
      case "entry/group/thumbnail@url": photo.thumbUrl = value; break;
    }
  }

  @Override
  public void start(String path) throws StopParse {
    if ("entry".equals(path)) photo = new Photo();
  }

  @Override
  public void end(String path) throws StopParse {
    if ("entry".equals(path)) album.photos.add(photo);
  }
}
