package net.azib.photos;

import java.text.SimpleDateFormat;

public class Entity {
  String title;
  String description;
  String thumbUrl;
  Long timestamp;
  GeoLocation geo;

  public String getDescription() {
    return description != null ? description : "";
  }

  public String getTimestampISO() {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(timestamp);
  }

  public String getTitle() {
    return title;
  }

  public String getThumbUrl() {
    return thumbUrl;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public GeoLocation getGeo() {
    return geo;
  }
}
