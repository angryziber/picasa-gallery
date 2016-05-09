package net.azib.photos;

import com.google.gdata.data.IFeed;

import java.util.Map;

public class CacheReloader implements Runnable {
  @Override public void run() {
    try {
      while (true) {
        Thread.sleep(60 * 60 * 1000); // 1h

        for (Map.Entry<String, IFeed> e : Picasa.cache.entrySet()) {
          IFeed feed = Picasa.load(e.getKey(), e.getValue().getClass());
          e.setValue(feed);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
