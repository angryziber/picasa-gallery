'use strict'

function initMap() {
  if (!window.google) return
  var bounds = new google.maps.LatLngBounds()
  var map = createMap('#map')
  $('.albums > a').each(function(i, link) {
    var pos = extractPos(this)
    if (!pos) return
    var marker = new google.maps.Marker({position: pos, map: map, title: $(link).find('.title > .text').text()})
    setMarkerIcon(marker)
    bounds.extend(pos)
    google.maps.event.addListener(marker, 'click', function() {$(link).click()})
    $(this).mouseover(function() {setMarkerIcon(marker, 'marker_orange')})
    $(this).mouseout(function() {setMarkerIcon(marker)})
  })

  if (bounds.isEmpty()) {
    map.setCenter(latLng(0, 0))
    map.setZoom(1)
  }
  else {
    map.fitBounds(bounds)
    map.panBy(0, 15)
  }
}

function setMarkerIcon(marker, name) {
  marker.setIcon('https://maps.google.com/mapfiles/' + (name || 'marker') + '.png')
  marker.setZIndex(1000)
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
