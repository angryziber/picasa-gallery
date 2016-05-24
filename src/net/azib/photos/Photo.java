package net.azib.photos;

import java.text.SimpleDateFormat;

public class Photo extends Entity {
  public String id;
  public Integer width;
  public Integer height;
  public String url;
  public Exif exif = new Exif();

  public String getDateTime() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
  }

  public String getId() {
    return id;
  }

  public Integer getWidth() {
    return width;
  }

  public Integer getHeight() {
    return height;
  }

  public String getUrl() {
    return url;
  }

  public Exif getExif() {
    return exif;
  }
}
