package net.azib.photos;

public class Comment {
  public String author;
  public String avatarUrl;
  public String authorId;
  public String text;
  public String photoId;

  public Object get(String name) throws NoSuchFieldException, IllegalAccessException {
    return getClass().getField(name).get(this);
  }
}
