package net.azib.photos;

import java.util.ArrayList;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class AlbumLoader implements XMLListener<Album> {
  private Album album;
  private Photo photo;
  private Comment comment;

  @Override public Album getResult() {
    return album;
  }

  @Override
  public void rootElement(String name) {
    album = new Album();
    photo = null;
    comment = null;
  }

  @Override
  public void rootElementEnd(String name) {
  }

  @Override
  public void value(String path, String value) throws StopParse {
    switch (path) {
      case "name": album.name = value; return;
      case "title": album.title = value; return;
      case "subtitle": album.description = value; return;
      case "icon": album.thumbUrl = value; return;
      case "timestamp": album.timestamp = parseLong(value); return;
      case "nickname": album.author = value; return;
      case "user": album.authorId = value; return;
      case "access": album.isPublic = "public".equals(value); return;
      case "numphotos": album.photos = new ArrayList<>(album.size = parseInt(value)); return;
      case "where/Point/pos": album.geo = new GeoLocation(value); return;

      case "entry/category@term":
        if (value.endsWith("photo")) album.photos.add(photo = new Photo());
        else if (value.endsWith("comment")) album.comments.add(comment = new Comment());
        return;
    }

    if (photo != null) {
      switch (path) {
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
    else if (comment != null) {
      switch (path) {
        case "entry/content": comment.text = value; break;
        case "entry/author/name": comment.author = value; break;
        case "entry/author/thumbnail": comment.avatarUrl = value; break;
        case "entry/author/user": comment.authorId = value; break;
        case "entry/photoid": comment.photoId = value; break;
      }
    }
  }

  @Override
  public void start(String path) throws StopParse {
  }

  @Override
  public void end(String path) throws StopParse {
    if ("entry".equals(path)) {
      photo = null;
      comment = null;
    }
  }
}
