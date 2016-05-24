package net.azib.photos;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends Entity {
  public String author;

  public List<Album> albums = new ArrayList<>(64);
}
