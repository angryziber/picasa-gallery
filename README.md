What is it?
===========

A good-looking frontend for Google's Picasaweb photo galleries, with nice transitions,
mobile device support, and full-screen photo browsing with quick loading (prefetching).

For all those who wish their Picasaweb gallery had black background!

Example gallery: [Anton Keks Photos](http://photos.azib.net/) - use the link at the bottom to try it with your own gallery!

How does it work?
=================

It's a small Java application made to be hosted for free on Google App Engine.
It uses Google Data API to fetch and display your albums and photos, so whenever you change anything
on Picasaweb, it will become visible in this gallery.

Features
========

- Dark theme that emphasizes photos
- Shows your exising Picasaweb albums, no additional storage needed
- Search within the gallery (by tags, keywords, descriptions, etc)
- Nice bookmarkable URLs for albums, individual photos, search results
- Gallery map if albums are geotagged, album map coming soon
- Full-screen photo viewer
- Fast: minimum number of requests, optimized caching, preloading of next photos
- No pagination (but only visible thumbnails are loaded)
- Keyboard navigation
- Mobile device support, eg iPhone, iPad, Android (including touch events)
- Facebook like buttons for albums and individual photos
- Opengraph metadata (for FB sharing, etc)
- Google analytics support
- Viewing of other people's albums, just add "?by=username" parameter
- Slideshow with adjustable delay, add "#slideshow" to the URL if you want it to start automatically
- Showing of a single (weighted) random photo from all albums, just add "?random" parameter

How to use it for your own photos
=================================

- Clone this repository as described on Github
- Specify your Picasaweb username in src/config.properties
- Specify your AppEngine application ID in web/WEB-INF/appengine-web.xml
- Download AppEngine SDK from Google
- If you don't use any IDE, use ant to compile by this command (use Java 7 or above):
  
  	    $ ant -Dsdk.dir=path/to/appengine-java-sdk compile

- Use AppEngine SDK / Eclipse / IntelliJ IDEA to upload your application.
  Details here: <https://developers.google.com/appengine/docs/java/gettingstarted/uploading>

P.S. the app is a standard Java Servlet-based application, so it will work without AppEngine as well.
