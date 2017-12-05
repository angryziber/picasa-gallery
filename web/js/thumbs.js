'use strict'

function ThumbsView(thumbSize) {

  function updateLayout() {
    if (!isDesktop()) return;
    var photoWidth = thumbSize + 10
    var maxWidth = window.innerWidth - 90
    var photosInRow = Math.floor(maxWidth / photoWidth)
    $('#content').width(photosInRow * photoWidth)
  }

  function isDesktop() {
    return window.innerWidth > 700
  }

  function isHdpi() {
    return isDesktop() && window.devicePixelRatio >= 2
  }

  function setSrc(img) {
    if (!img.src) {
      var src = img.dataset.src
      img.src = hdpi ? src.replace('/s' + thumbSize + '-c/', '/s' + (thumbSize * 2) + '-c/') : src
      delete img.dataset.src
    }
  }

  function loadVisibleThumbs() {
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

  function loadVisibleThumbsDebounce(e) {
    if (e.timeStamp - (loadVisibleThumbs.lastTimeStamp || 0) > 200) {
      loadVisibleThumbs.lastTimeStamp = e.timeStamp
      setTimeout(function() {
        if (loadVisibleThumbs() === false)
          $(window).off('resize scroll', loadVisibleThumbsDebounce)
      }, 0)
    }
  }

  var hdpi = isHdpi()
  updateLayout()
  $(loadVisibleThumbs)

  $(window).on('resize', updateLayout)
           .on('resize scroll', loadVisibleThumbsDebounce)
}
