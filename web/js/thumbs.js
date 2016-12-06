function ThumbsView(thumbSize) {
  function updateLayout() {
    var photoWidth = thumbSize + 10
    var maxWidth = $(window).width()
    if (maxWidth > 700) maxWidth -= 90
    var photosInRow = Math.floor(maxWidth / photoWidth)
    $('#content').width(photosInRow * photoWidth)
  }

  function isHdpi() {
    return window.devicePixelRatio >= 2
  }

  function loadThumbs(hdpi) {
    document.querySelectorAll('.thumbs img').forEach(function(img) {
      if (!img.src) {
        var src = img.dataset.src
        img.src = hdpi ? src.replace('/s' + thumbSize + '-c/', '/s' + (thumbSize * 2) + '-c/') : src
        delete img.dataset.src
      }
    })
  }

  updateLayout()
  loadThumbs(isHdpi())
  $(window).on('resize', updateLayout)
}
