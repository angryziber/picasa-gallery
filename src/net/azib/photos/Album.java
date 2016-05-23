package net.azib.photos;

import java.util.ArrayList;
import java.util.List;

public class Album {
  public String title;
  public String description;
  public String author;
  public String iconUrl;
  public Long timestamp;

  public List<Photo> photos = new ArrayList<>();
}
