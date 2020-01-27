package views

import photos.Album
import photos.AlbumPart

// language=HTML
fun albumPart(albumPart: AlbumPart, album: Album, bot: Boolean) = """
<div class="load-time hidden">${albumPart.photos.size} loaded at ${albumPart.loadedAt}</div>

${albumPart.photos.each {"""
  <a id="$id" class="photo" href="${album.url}/$id" data-url="${baseUrl?.url}"
    ${timestamp / """data-time="$dateTime""""}
    ${geo / """data-coords="${geo?.lat}:${geo?.lon}""""}
    ${exif / """data-exif="$exif""""}>
    <img ${bot / """src="$thumbUrl""""} ${description / """alt="$description""""}>
  </a>
"""}}

<script>
  viewer.addPhotos()
  ${if (albumPart.nextPageToken != null) """
    var thumbs = jQuery('.thumbs').append('<a class="album-part-loader"><div class="loader"></div></a>')
    jQuery.get(location.pathname + (location.search ? location.search + '&' : '?') + 'pageToken=${albumPart.nextPageToken}').then(function(html) {
      thumbs.find('.album-part-loader').remove()
      thumbs.append(html)
      thumbsView.loadVisibleThumbs()
    })
  """ else """
    thumbsView.loadingFinished = true
  """}  
</script>
"""
