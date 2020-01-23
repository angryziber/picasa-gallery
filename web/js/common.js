'use strict'

function fadeTo(href) {
  var content = $('#content').addClass('faded')
  setTimeout(function() {
    content.remove()
    $('body').append('<div class="loader"></div>')
    setTimeout(function() {
      location.href = href
    })
  }, 400)
}

function createMap(selector, moreOpts) {
  var options = {
    mapTypeId: google.maps.MapTypeId.TERRAIN,
    styles: [{
      stylers: [
        {saturation: -60},
        {gamma: 0.3},
        {lightness: -25}
      ]
    }],
    streetViewControl: false,
    zoomControl: false,
    panControl: false,
    mapTypeControl: false,
    minZoom: 1,
    backgroundColor: '#3e5e89'
  }
  $.extend(options, moreOpts)
  return new google.maps.Map($(selector)[0], options)
}

function latLng(lat, lon) {
  return window.google ? new google.maps.LatLng(lat, lon) : null
}

function extractPos(element) {
  var coords = $(element).data('coords')
  if (!coords) return null
  coords = coords.split(':')
  return latLng(coords[0], coords[1])
}

$(function() {
  $('#content').removeClass('faded')

  window.onpageshow = function(e) {
    // fix Mobile Safari back button navigation
    if (e.persisted) $('#content').removeClass('faded')
  }

  $('a.fade').on('click', function() {
    fadeTo(this.href)
    return false
  })
})
