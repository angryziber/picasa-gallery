package net.azib.photos;

import java.util.List;

public class RandomPhotos {
  private final List<Photo> photos;
  private final String author;
  private final String album;

  public RandomPhotos(List<Photo> photos, String author, String album) {
    this.photos = photos;
    this.author = author;
    this.album = album;

    for (Photo photo : photos) {
      photo.setUrl(photo.getUrl().replace("/s1600/", "/s1920/"));
    }
  }

  public List<Photo> getPhotos() {
    return photos;
  }

  public String getAuthor() {
    return author;
  }

  public String getAlbum() {
    return album;
  }
}
