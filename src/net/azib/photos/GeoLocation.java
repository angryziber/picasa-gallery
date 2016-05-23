package net.azib.photos;

import static java.lang.Float.parseFloat;

public class GeoLocation {
  private float lat;
  private float lon;

  public GeoLocation(String value) {
    String[] parts = value.split(" ");
    lat = parseFloat(parts[0]);
    lon = parseFloat(parts[1]);
  }

  public float getLat() {
    return lat;
  }

  public float getLon() {
    return lon;
  }
}
