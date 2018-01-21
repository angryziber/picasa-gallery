'use strict'

function GalleryMap() {
  var initialBounds, hoverZoom, zoomTimer

  function init() {
    if (!window.google) return
    initialBounds = new google.maps.LatLngBounds()
    var map = createMap('#map')
    $('.albums > a').each(function(i, link) {
      var pos = extractPos(this)
      if (!pos) return
      var marker = new google.maps.Marker({position: pos, map: map, title: $(link).find('.title > .text').text()})
      setMarkerIcon(marker)
      initialBounds.extend(pos)
      google.maps.event.addListener(marker, 'click', function() {$(link).click()})
      albumThumbHover($(this), map, marker)
    })

    var zoomListener = google.maps.event.addListener(map, 'zoom_changed', function() {
      hoverZoom = map.getZoom() + 3
      google.maps.event.removeListener(zoomListener)
    })

    if (initialBounds.isEmpty()) {
      map.setCenter(latLng(0, 0))
      map.setZoom(1)
    }
    else {
      map.fitBounds(initialBounds)
    }

    $(document).on('webkitfullscreenchange mozfullscreenchange fullscreenchange', function() {
      if (document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen) {
        if (map.getZoom() < hoverZoom) {
          map.setCenter(latLng(0, 0))
          map.setZoom(hoverZoom - 1)
        }
      }
    });
  }

  function albumThumbHover(thumb, map, marker) {
    thumb.on('mouseover', function() {
      setMarkerIcon(marker, 'marker_orange')
      clearTimeout(zoomTimer)
      zoomTimer = setTimeout(function() {
        if (map.getZoom() < hoverZoom) map.setZoom(hoverZoom)
        map.panTo(marker.getPosition())
      }, 500)
    })

    thumb.on('mouseout', function() {
      setMarkerIcon(marker)
      clearTimeout(zoomTimer)
      zoomTimer = setTimeout(function() {
        if (!$('#map').is(':hover'))
          map.fitBounds(initialBounds)
      }, 500)
    })
  }

  function setMarkerIcon(marker, name) {
    marker.setIcon('https://maps.google.com/mapfiles/' + (name || 'marker') + '.png')
    marker.setZIndex(1000)
  }

  init()
}

function initAlbumFilter() {
  var albums = {}
  $('.albums a').each(function() {
    albums[this.id] = $(this).text().toLowerCase()
  })
  $('#search input').keyup(function() {
    var q = $(this).val().toLowerCase()
    // Note: direct CSS is much faster than $.show() and $.hide()
    $.each(albums, function(id, text) {
      var matches = q.length < 3 || text.match(q)
      document.getElementById(id).style.display = matches ? 'block' : 'none'
    })
  })
}

function changeUsername(username) {
  username = prompt('Show photos by Google/Picasaweb user:', username)
  if (username) fadeTo('/?by=' + username)
}
