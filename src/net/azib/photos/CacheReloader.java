package net.azib.photos;

import net.azib.photos.Picasa.CacheEntry;

import java.io.IOException;
import java.util.Map;

public class CacheReloader {
  public void reload() throws IOException {
    for (Map.Entry<String, CacheEntry> e : Picasa.cache.entrySet()) {
      e.getValue().data = (Entity) Picasa.loadAndParse(e.getKey(), e.getValue().loader);
    }
  }
}
