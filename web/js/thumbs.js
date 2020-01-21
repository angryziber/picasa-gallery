'use strict'

function ThumbsView(thumbSize) {
  var self = this
  self.loadingFinished = false

  function updateLayout() {
    if (!isDesktop()) return
    var photoWidth = thumbSize + 10
    var maxWidth = window.innerWidth
    if (window.innerWidth > 900) maxWidth -= 90
    var photosInRow = Math.floor(maxWidth / photoWidth)
    $('#content').width(photosInRow * photoWidth)
  }

  function isDesktop() {
    return window.innerWidth > 700
  }

  function setSrc(img) {
    if (!img.src) {
      var baseUrl = img.parentElement.dataset.url
      if (baseUrl.endsWith('.jpg'))
        img.src = baseUrl + (pixelRatio >= 1.5 ? '?x2' : '')
      else
        img.src = baseUrl + '=w' + scaledThumbSize + '-h' + scaledThumbSize + '-c'
      delete img.parentElement.dataset.url
    }
  }

  self.loadVisibleThumbs = function() {
    var loadTop = window.scrollY - window.innerHeight
    var loadBottom = window.scrollY + window.innerHeight * 2

    var notLoadedThumbs = $('.thumbs img:not([src])')
    if (!notLoadedThumbs.length) return false

    var found = false
    notLoadedThumbs.each(function(i, img) {
      var top = $(img).offset().top
      if (top >= loadTop && top <= loadBottom) {
        setSrc(img)
        found = true
      }
      else if (found) return false
    })
  }

  var lastEventTimestamp = 0

  function loadVisibleThumbsDebounce(e) {
    if (e.timeStamp - lastEventTimestamp > 200) {
      lastEventTimestamp = e.timeStamp
      setTimeout(function() {
        if (self.loadVisibleThumbs() === false && self.loadingFinished)
          $(window).off('resize scroll', loadVisibleThumbsDebounce)
      }, 0)
    }
  }

  var pixelRatio = (isDesktop() ? window.devicePixelRatio : 1) || 1
  var scaledThumbSize = thumbSize * pixelRatio
  updateLayout()
  $(self.loadVisibleThumbs)

  $(window).on('resize', updateLayout)
           .on('resize scroll', loadVisibleThumbsDebounce)
}
