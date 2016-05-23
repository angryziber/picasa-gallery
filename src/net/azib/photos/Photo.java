package net.azib.photos;

public class Photo extends Entity {
  public String id;
  public Integer width;
  public Integer height;
  public String url;
  public Exif exif = new Exif();
}
