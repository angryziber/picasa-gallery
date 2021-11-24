package views

import integration.Profile
import photos.Album
import photos.AlbumPart
import photos.Config.startTime
import web.RequestProps

// language=HTML
fun album(album: Album, albumPart: AlbumPart, profile: Profile, req: RequestProps) = """
<!DOCTYPE html>
<html lang="en">
<head>
  <title>${+album.title} by ${+profile.name} - Photos</title>
  <meta name="description" content="${+album.title} photos by ${+profile.name}: ${+album.description}">
  <meta name="medium" content="image">
  <meta property="og:title" content="${+album.title} photos by ${+profile.name}">
  <meta property="og:image" content="https://${req.host}${album.thumbUrlLarge}">
  <link rel="image_src" href="https://${req.host}${album.thumbUrlLarge}">
  ${album.geo / """
    <meta property="og:latitude" content="${album.geo!!.lat}">
    <meta property="og:longitude" content="${album.geo!!.lon}"> 
  """}
  <meta property="og:description" content="${+album.description}">
  <meta property="og:site_name" content="${+profile.name} Photography">

  ${head(req, profile)}
  <script src="/js/album.js?$startTime"></script>
  <script>
    var viewer = new PhotoViewer();
    jQuery(function() {
      viewer.setup()
      if (location.hash === '#slideshow') {
        jQuery('a.photo').eq(0).click()
        setTimeout(viewer.slideshow, 500)
      }
    })
  </script>
</head>

<body style="background:black; color: gray">

<div id="header" class="header">
  <a href="${req.urlPrefix}" class="button fade">More albums</a>

  <h1 id="title">
    ${+album.title}
    <small>by ${+profile.name}</small>
  </h1>
</div>

<div id="content" class="faded">
  ${if (album.content != null) """
    ${if (album.contentIsLong) """
      <div id="album-long-content" class="${!req.bot / "closed"}">
        ${album.content}
      </div>
      <script>
        jQuery('#album-long-content')
          .on('mousedown', function() {
            jQuery(this).data('time', new Date())
          })
          .on('mouseup', function(e) {
            if (e.target.tagName !== 'A' && new Date() - jQuery(this).data('time') < 300)
              jQuery(this).toggleClass('closed')
          })
      </script> 
    """ else """
      ${album.content}
    """}    
  """ else if (album.description.isNotEmpty()) """
    <h1>${+album.title}</h1>
    <h2>${+album.description}</h2> 
  """ else ""}
  <br>

  <script>var thumbsView = new ThumbsView(144)</script>

  <div class="thumbs clear">
    ${albumPart(albumPart, album, req)}
  </div>

  <p id="footer">
    Photos by ${+profile.name}. All rights reserved.
    <br>
    Rendered by <a href="https://github.com/angryziber/picasa-gallery">Picasa Gallery</a>.
  </p>
</div>

<div id="photo-wrapper">
  <div class="loader"></div>
  <div class="title-wrapper">
    <div class="title"></div>
  </div>
  <div id="photo-map"></div>
  <table id="photo-exif">
    <tr>
      <td id="time" colspan="2"></td>
    </tr>
    <tr>
      <td id="aperture"></td>
      <td id="iso"></td>
    </tr>
    <tr>
      <td id="shutter"></td>
      <td id="focal"></td>
    </tr>
  </table>
  <div id="photo-comments">
    ${album.comments.each { """
      <div class="comment photo-$photoId hidden">
        <img src="$avatarUrl">
        ${+author}<br>${+text}
      </div>
    """}}
  </div>
  <div id="photo-controls" class="visible">
    <div class="header clearfix">
      <a class="button first" id="close" onclick="viewer.close()">Close</a>
      <a class="button" id="slideshow" onclick="viewer.slideshow()">Slideshow</a>
      <a class="button" id="dec-interval">-</a>
      <span class="left"><span id="interval">5</span> <span id="sec">sec</span></span>
      <a class="button" id="inc-interval">+</a>
      <span class="left" id="time-remaining"></span>

      <h1><span id="photo-controls-title">${+album.title}</span>
        <small id="position"></small>
      </h1>
    </div>
  </div>
</div>

${sharing()}

</body>
</html>
"""
