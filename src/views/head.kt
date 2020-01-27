package views

import integration.Profile
import photos.Config
import photos.Config.startTime
import web.RequestProps

// language=HTML
fun head(req: RequestProps, profile: Profile) = """
${req.urlSuffix.isNotEmpty() / """<meta name="robots" content="noindex">"""}  
<meta property="fb:admins" content="${profile.slug}"/>

<meta name="viewport" content="width=device-width">
<meta name="mobile-web-app-capable" content="yes">
<meta name="theme-color" content="#313131">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<link rel="apple-touch-icon" href="/img/picasa-logo.png"/>

<link rel="shortcut icon" href="/img/picasa-logo.png">
<link rel="manifest" href="/manifest.json">

<link rel="stylesheet" type="text/css" href="/css/reset.css">
<link rel="stylesheet" type="text/css" href="/css/gallery.css?$startTime">
<link rel="stylesheet" type="text/css" href="/css/scrollbars.css?$startTime">
<link rel="stylesheet" type="text/css" href="/css/loader.css?$startTime">

<script src="/js/jquery.min.js"></script>
<script src="/js/prefixfree.min.js"></script>
<script src="/js/common.js?$startTime"></script>
<script src="/js/thumbs.js?$startTime"></script>
${!req.bot / """
<script defer src="/js/chromecast.js"></script>
<script defer src="//maps.google.com/maps/api/js?key=${Config.mapsKey}"></script> 
"""}

${Config.analyticsId / """
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
          m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', '${Config.analyticsId}', 'auto');
  ga('send', 'pageview');
</script> 
"""}
"""
