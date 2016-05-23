package net.azib.photos;

import static java.lang.Float.parseFloat;

public class GeoLocation {
  public float lat;
  public float lon;

  public GeoLocation(String value) {
    String[] parts = value.split(" ");
    lat = parseFloat(parts[0]);
    lon = parseFloat(parts[1]);
  }
}
