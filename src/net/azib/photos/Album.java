package net.azib.photos;

import java.util.ArrayList;
import java.util.List;

public class Album extends Entity {
  String name;
  String author;
  String authorId;
  boolean isPublic;
  int size;

  public List<Photo> photos = new ArrayList<>();
  public List<Comment> comments = new ArrayList<>();

  public int size() {
    return size;
  }

  public String getName() {
    return name;
  }

  public String getAuthor() {
    return author;
  }

  public String getAuthorId() {
    return authorId;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public int getSize() {
    return size;
  }

  public List<Photo> getPhotos() {
    return photos;
  }

  public List<Comment> getComments() {
    return comments;
  }
}
