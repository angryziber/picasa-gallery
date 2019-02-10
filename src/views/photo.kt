package views

import integration.Profile
import photos.Album
import photos.Photo

//language=HTML
fun photo(photo: Photo, album: Album, profile: Profile, redirectUrl: String?) = """
<!DOCTYPE html>
<html lang="en">
<head>
  <title>${+album.title} - ${+photo.description} by ${+profile.name}</title>
  <meta name="viewport" content="width=device-width">
  <meta name="medium" content="image">
  <meta property="og:title" content="${+(photo.description ?: album.title)} by ${+profile.name}">
  <meta property="og:image" content="${+photo.thumbUrlLarge}">
  <link rel="image_src" href="${+photo.thumbUrlLarge}">
  <style>
    html, body { background: black; color: gray; font-family: sans-serif }
    a { color: white }
    img { padding: 1em 0; max-height: 90vh; max-width: 95vw; cursor: pointer }
  </style>
</head>
<body>
  <div itemscope itemtype="http://schema.org/Photograph">
    <meta itemprop="datePublished" content="${photo.date}">
    <a href="/${album.name}">${+album.title} by <span itemprop="author">${+profile.name}</span></a>
    <span itemprop="description">${+photo.description}</span>
    <div>
      <img itemprop="image" src="${photo.fullHdUrl}" alt="${+(photo.description ?: album.title)}">
    </div>
  </div>
  ${redirectUrl / """<script>location.href = '$redirectUrl'</script>""" }}
</body>
</html>
"""
