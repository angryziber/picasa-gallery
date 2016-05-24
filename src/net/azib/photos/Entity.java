package net.azib.photos;

import java.text.SimpleDateFormat;

public class Entity {
  public String title;
  public String description;
  public String thumbUrl;
  public Long timestamp;
  public GeoLocation geo;

  public String getDescription() {
    return description != null ? description : "";
  }

  public String getTimestampISO() {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(timestamp);
  }

  public Object get(String name) throws NoSuchFieldException, IllegalAccessException {
    return getClass().getField(name).get(this);
  }
}
