function ThumbsView(thumbSize, useHdpi) {
  function updateLayout() {
    var photoWidth = thumbSize + 10;
    var photosInRow = Math.floor(($(window).width()-90) / photoWidth)
    $('#content').width(photosInRow * photoWidth)
  }

  function isHdpi() {
    return window.devicePixelRatio >= 2
  }

  function loadThumbs(hdpi) {
    document.querySelectorAll('.thumbs img').forEach(function(img) {
      var src = img.src || img.getAttribute('data-src')
      img.src = hdpi ? src.replace('/s' + thumbSize + '-c/', '/s' + (thumbSize * 2) + '-c/') : src
    })
  }

  updateLayout()
  if (useHdpi) loadThumbs(isHdpi())
  $(window).on('resize', updateLayout)
}
