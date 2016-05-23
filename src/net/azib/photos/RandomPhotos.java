package net.azib.photos;

import com.google.gdata.data.MediaContent;
import com.google.gdata.data.photos.GphotoEntry;

import java.util.List;

public class RandomPhotos {
  private final List<GphotoEntry> photos;
  private final String nickname;
  private final String album;

  public RandomPhotos(List<GphotoEntry> photos, String nickname, String album) {
    this.photos = photos;
    this.nickname = nickname;
    this.album = album;

    for (GphotoEntry photo : photos) {
      MediaContent content = (MediaContent) photo.getContent();
      content.setUri(content.getUri().replace("/s1600/", "/s1920/"));
    }
  }

  public List<GphotoEntry> getPhotos() {
    return photos;
  }

  public String getNickname() {
    return nickname;
  }

  public String getAlbum() {
    return album;
  }
}
