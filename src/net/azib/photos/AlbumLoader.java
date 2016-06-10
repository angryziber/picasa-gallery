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
      case "name": album.setName(value); return;
      case "title": album.setTitle(value); return;
      case "subtitle": album.setDescription(value); return;
      case "icon": album.setThumbUrl(value); return;
      case "timestamp": album.setTimestamp(parseLong(value)); return;
      case "nickname": album.setAuthor(value); return;
      case "user": album.setAuthorId(value); return;
      case "access": album.setPublic("public".equals(value)); return;
      case "numphotos": album.setSize(parseInt(value));
                        album.setPhotos(new ArrayList<Photo>(album.getSize())); return;
      case "where/Point/pos": album.setGeo(new GeoLocation(value)); return;

      case "entry/category@term":
        if (value.endsWith("photo")) album.getPhotos().add(photo = new Photo());
        else if (value.endsWith("comment")) album.getComments().add(comment = new Comment());
        return;
    }

    if (photo != null) {
      switch (path) {
        case "entry/id": photo.setId(value); break;
        case "entry/title": photo.setTitle(value); break;
        case "entry/summary": photo.setDescription(value); break;
        case "entry/timestamp": photo.setTimestamp(parseLong(value)); break;
        case "entry/width": photo.setWidth(parseInt(value)); break;
        case "entry/height": photo.setHeight(parseInt(value)); break;
        case "entry/content@src": photo.setUrl(value); break;
        case "entry/group/thumbnail@url": photo.setThumbUrl(value); break;
        case "entry/tags/fstop": photo.getExif().fstop = parseFloat(value); break;
        case "entry/tags/exposure": photo.getExif().exposure = parseFloat(value); break;
        case "entry/tags/focallength": photo.getExif().focal = parseFloat(value); break;
        case "entry/tags/iso": photo.getExif().iso = value; break;
        case "entry/tags/model": photo.getExif().camera = value; break;
        case "entry/where/Point/pos": photo.setGeo(new GeoLocation(value)); break;
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
