package views

import integration.Profile
import photos.Config.startTime
import photos.Picasa

//language=HTML
fun gallery(picasa: Picasa, profile: Profile, host: String, bot: Boolean) = """
<!DOCTYPE html>
<html lang="en">
<head>
  <title>${+profile.name} Photography</title>
  <meta name="description" content="${+profile.name} photo albums from around the World">
  <meta property="og:title" content="${+profile.name} Photography">
  <meta property="og:image" content="https://${host}${picasa.gallery.albums.values.first().thumbUrlLarge}">
  <meta property="og:site_name" content="${+profile.name} Photography">
  ${head(picasa, profile, bot)}
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
    ${picasa.gallery.albums.values.each { """
      <a id="${+name}" class="fade" href="${url}${picasa.urlSuffix}"
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
    <a href="?random${picasa.urlSuffix.replace('?', '&')}" rel="nofollow">Random photo</a>.
    <br>
    Rendered by <a href="https://github.com/angryziber/picasa-gallery">Picasa Gallery</a>.
    View your <a href="/oauth" rel="nofollow">own gallery</a>.
  </p>
</div>

${sharing()}

<script>new ThumbsView(${picasa.gallery.albums.values.first().thumbSize})</script>

<div class="load-time hidden">${picasa.gallery.loadedAt}</div>

</body>
</html>
"""
