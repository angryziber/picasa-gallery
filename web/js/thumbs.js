function ThumbsView(thumbSize, useHdpi) {
  function updateLayout() {
    var photoWidth = thumbSize + 10;
    var photosInRow = Math.floor(($(window).width()-90) / photoWidth)
    $('#content').width(photosInRow * photoWidth)
  }

  function isHdpi() {
    return useHdpi && window.devicePixelRatio >= 2
  }

  function useHdpiThumbs() {
    $('.thumbs img').each(function () {
      this.src = this.src.replace('/s' + thumbSize + '-c/', '/s' + (thumbSize * 2) + '-c/')
    })
  }

  updateLayout()
  if (isHdpi()) useHdpiThumbs()
  $(window).on('resize', updateLayout)
}
