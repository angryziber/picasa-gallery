What is it?
===========

A good-looking frontend for Google Photos (aka Google+ or Picasa web) galleries, with nice transitions,
mobile device support, and full-screen photo browsing with quick loading (prefetching).

Example gallery: [Anton Keks Photos](http://photos.azib.net/) - use the link at the bottom to try it with your own gallery!

How does it work?
=================

It's a small app made to be hosted for free on Google App Engine.
It uses Google Data API to fetch and display your albums and photos, so whenever you change anything
on Google Photos/Picasaweb, it will become visible in this gallery.

Features
========

- Dark theme that emphasizes photos
- Shows your exising public albums, no additional storage needed
- Search within the gallery (by tags, keywords, descriptions, etc)
- Nice bookmarkable URLs for albums, individual photos, search results
- Gallery map if albums are geotagged
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

How to use it for your own photos
=================================

- Clone this repository as described on Github
- Specify your Google username in src/config.properties
- Specify your AppEngine application ID in web/WEB-INF/appengine-web.xml
- Download AppEngine SDK from Google
- If you don't use any IDE:
  * `./gradlew war` - will build the war file (web application archive)
  * `./gradlew appengineRun` - will run the app locally on port 8080
  * `./gradlew appengineUpdate` - will upload the app to Google AppEngine

P.S. the app is a standard Java Servlet-based application, so it will work without AppEngine as well.
