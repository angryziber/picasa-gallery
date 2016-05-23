package net.azib.photos;

import java.util.ArrayList;
import java.util.List;

public class Album extends Entity {
  public String author;
  public boolean isPublic;

  public List<Photo> photos = new ArrayList<>();
}
