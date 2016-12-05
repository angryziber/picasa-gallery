function ThumbsView(thumbSize) {
  function updateLayout() {
    var photoWidth = thumbSize + 10;
    var photosInRow = Math.floor(($(window).width()-90) / photoWidth);
    $('#content').width(photosInRow * photoWidth);
  }

  updateLayout();
  $(window).on('resize', updateLayout);
}
