'use strict'

function fadeTo(href) {
  $('#content').addClass('faded')
  setTimeout(function() {
    location.href = href
  }, 500)
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
    minZoom: 1
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

  $('form#search').on('submit', function() {
    fadeTo('/?q=' + $(this).find('input').val() + location.search.replace(/\?q=.*?(&|$)/, '&'))
    return false
  })
})
