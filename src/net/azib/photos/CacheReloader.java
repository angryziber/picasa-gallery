package net.azib.photos;

import com.google.gdata.data.IFeed;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.util.Map;

public class CacheReloader {
  public void reload() throws IOException, ServiceException {
    for (Map.Entry<String, IFeed> e : Picasa.cache.entrySet()) {
      IFeed feed = Picasa.load(e.getKey(), e.getValue().getClass());
      e.setValue(feed);
    }
  }
}
