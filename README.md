What is it?
===========

A good-looking public frontend for shared albums stored in Google Photos (former Google+ or Picasa web), with nice transitions,
mobile device support, and full-screen photo browsing.

Perfect for those who still want to have a public gallery of albums after transition to Google Photos, which has no public
gallery of it's own anymore.

Example gallery: [Anton Keks Photos](https://photos.azib.net/) - use the link at the bottom to try it with your own gallery!

How does it work?
=================

It's a small app made to be hosted for free on Google App Engine.
It uses [Google Photos API](https://developers.google.com/photos/) to fetch and display your albums and photos. 
After the introduction of this new API, unfortunately, all requests must be authenticated.

Features
========

- Dark theme that emphasizes photos
- Shows your shared albums
- HDPI/Retina screens use hi-res thumbnails
- Nice shareable URLs for albums, individual photos, search results
- Gallery map if albums are geotagged - *geotagging is not available in the API - use web/content to provide location data*
- Optional content/blog in Markdown format - put it into `web/content`, see [content branch](https://github.com/angryziber/picasa-gallery/tree/content) for a sample
- Full-screen photo viewer
- Fast: minimum number of API requests, optimized caching, preloading of next photos - *but the new Google Photos API is quite slow by itself*
- Keyboard navigation
- Mobile device support, eg iPhone, iPad, Android (including touch events)
- Facebook/Instagram/Twitter/Pinterest sharing for albums and individual photos
- Opengraph metadata (for FB sharing, etc)
- Google analytics support
- Slideshow with adjustable delay, add "#slideshow" to the URL if you want it to start automatically
- Showing of a single (weighted) random photo from all albums, just add "?random" parameter
- ChromeCast support - send currently viewed photo to the TV

How to use it for your own photos [![Actions Status](https://github.com/angryziber/picasa-gallery/workflows/CI/badge.svg)](https://github.com/angryziber/picasa-gallery/actions)
=================================

- Clone this repository as described on Github
- Specify some properties e.g. OAuth refresh token, Google Maps key, etc in `src/config.properties`
- To authorize the gallery with your Google account and get refresh token, launch the app and navigate to `/oauth`
- Specify your AppEngine application ID in web/WEB-INF/appengine-web.xml
- Uploading and testing
  * `./gradlew appengineDeploy` - will upload the app to Google AppEngine
  * `./gradlew appengineRun` - will run the app locally on port 8080
- Or, the app is a standard Java Servlet-based application, so it will work with any other Servlet Container  
  * `./gradlew war` - will build the war file (web application archive)

- If you use Intellij IDEA, you can import `build.gradle` and run all the actions from an IDE. 

## Picasaweb retirement and Google Photos API limitations

Now the Picasaweb API has been finally replaced by the new Google Photos API, which unlike the old API,
is not tailored to public sharing. This app overcomes this by authorizing the access with your Google
account and provides the `web/content` (see above) to select visible albums and provide location and other metadata
that Google Photos API no longer returns.

Unfortunately, as of now, Google Photos API is quite slow, with response sizes limited and taking several seconds to complete.
Probably the reason is that it now generates expiring image URLs (for ~1 hour).
This limitation also makes it harder to cache and reload efficiently. Also, location metadata is not provided and even
stripped from the embedded Exif. Hopefully these issues will be resolved in future.
