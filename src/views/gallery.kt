package views

import integration.Profile
import photos.Config.startTime
import photos.Gallery
import web.RequestProps

//language=HTML
fun gallery(req: RequestProps, gallery: Gallery, profile: Profile) = """
<!DOCTYPE html>
<html lang="en">
<head>
  <title>${+profile.name} Photography</title>
  <meta name="description" content="${+profile.name} photo albums from around the World">
  <meta property="og:image" content="https://${req.host}${gallery.albums.values.first().thumbUrlLarge}">
  <meta property="og:description" content="${+profile.name} Photography">
  ${head(req, profile)}
  <script src="/js/gallery.js?$startTime"></script>
  <script>jQuery(GalleryMap)</script>
</head>

<body style="background:black; color: gray">

<div id="header" class="header">
  <h1 id="title">${+profile.name} Photography</h1>
</div>

<div id="content" class="faded">
  <div id="map"></div>
  <div class="albums thumbs">
    ${gallery.albums.values.each { """
      <a id="${+name}" class="fade" href="${url}${req.urlSuffix}"
         ${geo / """data-coords="${geo!!.lat}:${geo!!.lon}""""}
         data-url="${url}.jpg">
        <img alt="${+title} photos" title="${+description}">

        <div class="title">
          <span class="info">${size()}</span>
          <span class="text">${+title}</span>
        </div>
      </a>
    """}}
  </div>

  <p id="footer">
    Photos by ${profile.name}. All rights reserved.
    <a href="?random${req.urlSuffix.replace('?', '&')}" rel="nofollow">Random photo</a>.
    <br>
    Rendered by <a href="https://github.com/angryziber/picasa-gallery">Picasa Gallery</a>.
    View your <a href="/oauth" rel="nofollow">own gallery</a>.
  </p>
</div>

${sharing()}

<script>new ThumbsView(${gallery.albums.values.first().thumbSize})</script>

<div class="load-time hidden">${gallery.loadedAt}</div>

</body>
</html>
"""
