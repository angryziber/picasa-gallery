package net.azib.photos;

public class Entity {
  public String title;
  public String description;
  public String thumbUrl;
  public Long timestamp;
  public GeoLocation geo;

  public String getDescription() {
    return description != null ? description : "";
  }

  public Object get(String name) throws NoSuchFieldException, IllegalAccessException {
    return getClass().getField(name).get(this);
  }
}
