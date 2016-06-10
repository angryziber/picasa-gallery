package net.azib.photos

import org.junit.Assert.assertTrue
import org.junit.Test

class RequestRouterTest {
  @Test fun botDetection() {
    assertTrue(RequestRouter.isBot("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"))
    assertTrue(RequestRouter.isBot("Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)"))
    assertTrue(RequestRouter.isBot("Mozilla/5.0 (compatible; AhrefsBot/5.0; +http://ahrefs.com/robot/)"))
    assertTrue(RequestRouter.isBot("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)"))
    assertTrue(RequestRouter.isBot("Sogou web spider/4.0(+http://www.sogou.com/docs/help/webmasters.htm#07)"))
  }
}
