What is it?
===========

A good-looking frontend for albums stored in Google Photos (former Google+ or Picasa web), with nice transitions,
mobile device support, and full-screen photo browsing with quick loading (prefetching).

Perfect for those who still want to have a public gallery of albums after transition to Google Photos, which has no public
gallery of it's own anymore.

Example gallery: [Anton Keks Photos](https://photos.azib.net/) - use the link at the bottom to try it with your own gallery!

How does it work?
=================

It's a small app made to be hosted for free on Google App Engine.
It uses [Google Web Albums Data API](https://developers.google.com/picasa-web) to fetch and display your albums and photos, so whenever you change anything
on Google Photos/Picasaweb, it will become visible in this gallery.

Features
========

- Dark theme that emphasizes photos
- Shows your existing public albums, no additional storage needed
- HDPI/Retina screens use hi-res thumbnails
- Search within the gallery (by tags, keywords, descriptions, etc) - *currently not working because Google removed this feature from the API*
- Nice shareable URLs for albums, individual photos, search results
- Gallery map if albums are geotagged
- Optional content/blog in Markdown format - put it into `web/content`, see [content branch](https://github.com/angryziber/picasa-gallery/tree/content) for a sample
- Full-screen photo viewer
- Fast: minimum number of requests, optimized caching, preloading of next photos
- Keyboard navigation
- Mobile device support, eg iPhone, iPad, Android (including touch events)
- Facebook Like buttons for albums and individual photos
- Opengraph metadata (for FB sharing, etc)
- Google analytics support
- Viewing of other people's albums, just add "?by=username" parameter
- Slideshow with adjustable delay, add "#slideshow" to the URL if you want it to start automatically
- Showing of a single (weighted) random photo from all albums, just add "?random" parameter
- ChromeCast support - send currently viewed photo to the TV (provided you own the ChromeCast dongle)

How to use it for your own photos [![Build Status](https://travis-ci.org/angryziber/picasa-gallery.svg?branch=master)](https://travis-ci.org/angryziber/picasa-gallery)
=================================

- Clone this repository as described on Github
- Specify your Google username, Google Maps key, etc in `src/config.properties`
- Specify your AppEngine application ID in web/WEB-INF/appengine-web.xml
- Uploading and testing
  * `./gradlew appengineUpdate` - will upload the app to Google AppEngine
  * `./gradlew appengineRun` - will run the app locally on port 8080
- Or, the app is a standard Java Servlet-based application, so it will work with any other Servlet Container  
  * `./gradlew war` - will build the war file (web application archive)

- If you use Intellij IDEA, you can import `build.gradle` and run all the actions from an IDE. 

## Picasaweb retirement

Since the beginning of 2016, Google has started removing features from Picasaweb API, and now the
whole Picasaweb service has been closed, being replaced by Album Archive.

This gallery still works for your old *public* albums stored in the archive (or just Google Photos).

However, when you upload new albums to Google Photos, there is no way anymore to mark them as *public*.
It seems that Google Photos right now is built to be mostly private photo storage service.

As previous workarounds don't work anymore, you now can still show your non-public albums by authenticating
with this app and adding names of *protected* albums to `web/content`. Unfortunately, there is currently no
way of telling if album is private or shared in Google Photos using the API.
See the comments in `config.properties` on how to authenticate.
