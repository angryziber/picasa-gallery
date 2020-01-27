package views

import photos.Album
import photos.AlbumPart
import web.RequestProps

// language=HTML
fun albumPart(albumPart: AlbumPart, album: Album, req: RequestProps) = """
<div class="load-time hidden">${albumPart.photos.size} loaded at ${albumPart.loadedAt}</div>

${albumPart.photos.each {"""
  <a id="$id" class="photo" href="${album.url}/$id" data-url="${baseUrl?.url}"
    ${timestamp / """data-time="$dateTime""""}
    ${geo / """data-coords="${geo?.lat}:${geo?.lon}""""}
    ${exif / """data-exif="$exif""""}>
    <img ${req.bot / """src="$thumbUrl""""} ${description / """alt="$description""""}>
  </a>
"""}}

<script>
  viewer.addPhotos()
  thumbsView.loadMore('${+albumPart.nextPageToken}')
</script>
"""
