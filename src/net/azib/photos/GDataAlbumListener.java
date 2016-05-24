package net.azib.photos;

import java.util.ArrayList;

import static java.lang.Float.parseFloat;
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
      case "name": album.name = value; break;
      case "title": album.title = value; break;
      case "subtitle": album.description = value; break;
      case "icon": album.thumbUrl = value; break;
      case "timestamp": album.timestamp = parseLong(value); break;
      case "nickname": album.author = value; break;
      case "access": album.isPublic = "public".equals(value); break;
      case "numphotos": album.photos = new ArrayList<>(parseInt(value)); break;
      case "where/Point/pos": album.geo = new GeoLocation(value); break;

      case "entry/id": photo.id = value; break;
      case "entry/title": photo.title = value; break;
      case "entry/summary": photo.description = value; break;
      case "entry/timestamp": photo.timestamp = parseLong(value); break;
      case "entry/width": photo.width = parseInt(value); break;
      case "entry/height": photo.height = parseInt(value); break;
      case "entry/content@src": photo.url = value; break;
      case "entry/group/thumbnail@url": photo.thumbUrl = value; break;
      case "entry/tags/fstop": photo.exif.fstop = parseFloat(value); break;
      case "entry/tags/exposure": photo.exif.exposure = parseFloat(value); break;
      case "entry/tags/focallength": photo.exif.focal = parseFloat(value); break;
      case "entry/tags/iso": photo.exif.iso = value; break;
      case "entry/tags/model": photo.exif.camera = value; break;
      case "entry/where/Point/pos": photo.geo = new GeoLocation(value); break;
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
