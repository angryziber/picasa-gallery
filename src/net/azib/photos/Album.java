package net.azib.photos;

import java.util.ArrayList;
import java.util.List;

public class Album extends Entity {
  public String name;
  public String author;
  public String authorId;
  public boolean isPublic;
  int size;

  public List<Photo> photos = new ArrayList<>();
  public List<Comment> comments = new ArrayList<>();

  public int size() {
    return size;
  }
}
