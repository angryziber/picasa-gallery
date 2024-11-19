'use strict'

function GalleryMap() {
  var hoverZoom, zoomTimer

  function init() {
    if (!window.google) return
    var map = createMap('#map')
    $('.albums > a').each(function(i, link) {
      var pos = extractPos(this)
      if (!pos) return
      var marker = new google.maps.Marker({position: pos, map: map, title: $(link).find('.title > .text').text()})
      setMarkerIcon(marker)
      google.maps.event.addListener(marker, 'click', function() {$(link).click()})
      albumThumbHover($(this), map, marker)
    })

    var zoomListener = google.maps.event.addListener(map, 'zoom_changed', function() {
      hoverZoom = map.getZoom() + 3
      google.maps.event.removeListener(zoomListener)
    })

    resetBounds(map)

    $(document).on('fullscreenchange', function() {
      if (document.fullscreenElement) {
        if (map.getZoom() < hoverZoom) {
          map.setCenter(latLng(0, 0))
          map.setZoom(hoverZoom - 1)
        }
      }
    });
  }

  function resetBounds(map) {
    map.setCenter(latLng(0, 0))
    map.setZoom(0)
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
          resetBounds(map)
      }, 500)
    })
  }

  function setMarkerIcon(marker, name) {
    marker.setIcon('https://maps.google.com/mapfiles/' + (name || 'marker') + '.png')
    marker.setZIndex(1000)
  }

  init()
}
