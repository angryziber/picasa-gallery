package net.azib.photos;

import java.io.IOException;
import java.util.Map;

public class CacheReloader {
  public void reload() throws IOException {
    for (Map.Entry<String, Entity> e : Picasa.cache.entrySet()) {
//      Entity feed = Picasa.loadAndParse(e.getKey(), e.getValue().getClass());
//      e.setValue(feed);
    }
  }
}
