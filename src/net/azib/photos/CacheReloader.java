package net.azib.photos;

import java.io.IOException;
import java.util.Map;

public class CacheReloader {
  public void reload() throws IOException {
    for (Map.Entry<String, XMLListener> e : Picasa.cache.entrySet()) {
      Picasa.loadAndParse(e.getKey(), e.getValue());
    }
  }
}
