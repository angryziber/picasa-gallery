'use strict'

function Loader(wrapper) {
  var el = $('.loader', wrapper)
  var timer

  this.show = function() {
    timer = setTimeout(function() {
      el.show()
    }, 200)
  }

  this.hide = function() {
    clearTimeout(timer)
    el.hide()
  }
}

function PhotoMap() {
  var el = $('#photo-map')
  var map, marker

  this.show = function(pos) {
    if (!map) {
      map = createMap(el, {mapTypeControl: false})
      map.setCenter(pos)
      map.setZoom(12)
      marker = new google.maps.Marker({position: pos, map: map})
    }
    else {
      marker.setPosition(pos)
      if (map.getBounds() && !map.getBounds().contains(pos))
        map.panTo(pos)
    }
    el.fadeIn()
  }

  this.hide = function() {
    el.fadeOut()
  }
}

function PhotoViewer() {
  var pub = this
  var w = $(window)
  var wrapper, title, controls, position, interval, timeRemaining, exif
  var slideshow = null
  var photos = []
  var index = 0
  var isOpen = false
  var loader, photoMap

  pub.setup = function() {
    $(document).on('click', 'a.photo', pub.open);

    wrapper = $('#photo-wrapper')
    wrapper.off()
    wrapper.on('click', onMouseClick)
    wrapper.on('mousemove', onMouseMove)
    controls = wrapper.find('#photo-controls')
    controls.find('#inc-interval').on('click', incInterval)
    controls.find('#dec-interval').on('click', decInterval)
    position = controls.find('#position')
    interval = controls.find('#interval')
    timeRemaining = controls.find('#time-remaining')
    exif = wrapper.find('#photo-exif')

    title = wrapper.find('.title')
    title.hover(function() {
      title.fadeOut()
    })

    loader = new Loader(wrapper)
    photoMap = new PhotoMap()
    return this
  }

  pub.addPhotos = function() {
    $('a.photo').slice(photos.length).each(function() {
      var title = $('img', this).attr('alt')
      photos.push({
        url: this.dataset.url,
        title: title,
        id: this.id,
        pos: extractPos(this),
        exif: extractExif(this),
        time: this.dataset.time
      })
      this.href = '#' + this.id
      if (title) this.setAttribute('title', title)
    })
    if (location.hash) $('a' + location.hash).click();
  }

  pub.open = function() {
    if (isOpen) return false
    isOpen = true
    onResize()
    $(document).on('keyup', keyHandler)
    $(window).on('resize', onResize);
    window.onpopstate = pub.close;
    wrapper[0].ontouchstart = onTouchStart
    wrapper[0].ontouchmove = onTouchMove

    index = $('a.photo').index(this)
    wrapper.find('img.photo').remove()
    wrapper.fadeIn()
    setTimeout(function() {
      controls.removeClass('visible')
    }, 2000)
    requestFullScreen()

    if (history.pushState) history.pushState('open', null)
    loadPhoto()
    return false
  }

  pub.close = function() {
    isOpen = false
    wrapper.fadeOut(function() {
      controls.addClass('visible')
    })
    $(document).off('keyup', keyHandler)
    $(window).off('resize', onResize)
    window.onpopstate = null

    stopSlideshow()
    if (history.state) history.back()
    var img = wrapper.find('img.photo')
    var thumb = $('#' + photos[index].id)
    img.animate({
      top: thumb.offset().top - w.scrollTop(),
      left: thumb.offset().left,
      height: thumb.height(),
      width: thumb.width()
    }, 500, function() {
      img.remove()
    })
  }

  pub.next = function() {
    index++
    if (index >= photos.length) index = 0
    loadPhoto()
  }

  pub.prev = function() {
    index--
    if (index < 0) index = photos.length - 1
    loadPhoto()
  }

  pub.first = function() {
    index = 0
    loadPhoto()
  }

  pub.last = function() {
    index = photos.length - 1
    loadPhoto()
  }

  pub.slideshow = function() {
    if (slideshow) stopSlideshow()
    else startSlideshow()
    return false
  }

  var requestFullScreen = function() {
    var el = document.documentElement
    var rfs = (el.requestFullscreen || el.webkitRequestFullscreen || el.mozRequestFullScreen || el.msRequestFullscreen)
    if (rfs) rfs.call(el)
    requestFullScreen = $.noop
  }

  function stateURL(photo) {
    return photo ? '#' + photo.id : location.href.replace(/#.*/, '')
  }

  function showTimeRemaining() {
    var sec = (photos.length - index - 1) * interval.text()
    var min = 0
    if (sec > 60) {
      min = Math.floor(sec / 60)
      sec = sec % 60
    }
    timeRemaining.text((min ? min + ' min ' : '') + sec + ' sec')
  }

  function setSlideshowTimeout() {
    if (slideshow) clearTimeout(slideshow)
    slideshow = setTimeout(function() {
      pub.next()
      showTimeRemaining()
    }, interval.text() * 1000)
  }

  function startSlideshow() {
    setSlideshowTimeout()
    controls.find('#slideshow.button').text('Stop')
    showTimeRemaining()
  }

  function stopSlideshow() {
    clearTimeout(slideshow)
    slideshow = null
    controls.find('#slideshow.button').text('Slideshow')
    timeRemaining.empty()
  }

  function incInterval() {
    interval.text(parseInt(interval.text()) + 1)
    if (slideshow) {
      stopSlideshow()
      startSlideshow()
    }
    return false
  }

  function decInterval() {
    interval.text(Math.max(1, parseInt(interval.text()) - 1))
    if (slideshow) {
      stopSlideshow()
      startSlideshow()
    }
    return false
  }

  function posAction(x, y) {
    var img = wrapper.find('img.photo')
    if (!img.length) return pub.close
    var left = img.offset().left
    var right = left + img.width()
    var delta = img.width() / 4
    if (x >= left - 20 && x <= left + delta) return pub.prev
    else if (x >= right - delta && x <= right + 20) return pub.next
    else return pub.close
  }

  var lastMousePos

  function onMouseMove(e) {
    var newMousePos = e.pageX + ":" + e.pageY
    if (lastMousePos != newMousePos) {
      var action = posAction(e.pageX, e.pageY)
      var cursor = action == pub.prev ? 'url(/img/left-cursor.png),w-resize' : action == pub.next ? 'url(/img/right-cursor.png),e-resize' : 'default'
      wrapper.css('cursor', cursor)
    }
    lastMousePos = newMousePos
  }

  function onMouseClick(e) {
    if ($('#photo-map:hover, #photo-comments:hover, #photo-controls:hover, a:hover').length) return true
    posAction(e.pageX, e.pageY)()
    return false
  }

  var touchStartX, touchStartY

  function onTouchStart(e) {
    touchStartX = e.touches[0].pageX
    touchStartY = e.touches[0].pageY
  }

  function onTouchMove(e) {
    if (!touchStartX) return false
    var dx = e.touches[0].pageX - touchStartX
    var dy = e.touches[0].pageY - touchStartY
    if (dx > 20 || dy > 20) {
      pub.prev()
      touchStartX = null
    }
    else if (dx < -20 || dy < -20) {
      pub.next()
      touchStartX = null
    }
    return false
  }

  function keyHandler(e) {
    switch (e.which) {
      case 81: // q
      case 27: pub.close(); return false
      case 32:
      case 34:
      case 40:
      case 39: pub.next(); return false
      case 33:
      case 38:
      case 37: pub.prev(); return false
      case 36: pub.first(); return false
      case 35: pub.last(); return false
      default: return true
    }
  }

  function onResize() {
    centerImage()
  }

  function centerImage(img) {
    if (!img) img = wrapper.find('img.photo')
    if (!img.length) return

    var photo = photos[index]
    var ww = wrapper.width(), wh = wrapper.height()
    if (photo.width > ww || photo.height > wh) {
      if (ww / wh > photo.width / photo.height) {
        img.attr('height', wh)
        img.removeAttr('width')
      }
      else {
        img.attr('width', ww)
        img.removeAttr('height')
      }
    }
    img.css('top', (wrapper.height() - img.height()) / 2)
    img.css('left', (wrapper.width() - img.width()) / 2)
  }

  function imageOnLoad() {
    loader.hide()
    var photo = photos[index]
    photo.width = this.width
    photo.height = this.height
    var img = $(this)
    wrapper.append(img)
    centerImage(img)
    img.fadeIn(function() {
      updateScrollPosition(photo)
    })
    wrapper.css('cursor', 'none')

    if (slideshow)
      setSlideshowTimeout()

    // preload next image
    if (index < photos.length - 1)
      setTimeout(function() {
        var tmp = new Image()
        tmp.src = photoUrl(photos[index + 1])
      }, 100)
  }

  function photoUrl(photo) {
    var ratio = window.devicePixelRatio || 1
    var imgMaxWidth = Math.round(window.innerWidth * ratio)
    var imgMaxHeight = Math.round(window.innerHeight * ratio)
    return photo.url + '=w' + imgMaxWidth + '-h' + imgMaxHeight
  }

  function loadPhoto() {
    loader.show()
    wrapper.css('cursor', 'wait')
    wrapper.find('img.photo').fadeOut(function() {
      $(this).remove()
    })

    var photo = photos[index]
    var newImg = new Image()
    newImg.className = 'photo'
    newImg.style.display = 'none'
    newImg.onload = imageOnLoad
    newImg.src = photoUrl(photo)
    if ('chromecast' in window) chromecast.send(newImg.src)

    title.text(photo.title)
    if (photo.title) title.fadeIn()
    else title.fadeOut()

    if (history.replaceState) history.replaceState(stateURL(photo), photo.title, stateURL(photo))
    if ('ga' in window) ga('send', 'event', location.pathname, location.hash)

    position.text((index + 1) + ' of ' + photos.length)

    wrapper.find('.comment').fadeOut()
    wrapper.find('.comment.photo-' + photo.id).fadeIn()

    exif.find('#time').text(photo.time)
    if (photo.exif) {
      exif.find('#aperture').toggle(!!photo.exif.aperture).text('f/' + photo.exif.aperture)
      exif.find('#shutter').toggle(!!photo.exif.shutter).text(photo.exif.shutter < 1 ? ('1/' + Math.round(1 / photo.exif.shutter)) : (photo.exif.shutter + '"'))
      exif.find('#iso').toggle(!!photo.exif.iso).text('ISO' + photo.exif.iso)
      exif.find('#focal').toggle(!!photo.exif.focal).text(photo.exif.focal + 'mm')
    }
    else
      exif.find('td').empty()

    if (photo.pos)
      photoMap.show(photo.pos)
    else
      photoMap.hide()
  }

  function updateScrollPosition(photo) {
    setTimeout(function() {
      var thumbPos = $('#' + photo.id).offset()
      var scrollTop = w.scrollTop()
      if (thumbPos.top + 150 > scrollTop + w.height())
        scrollTo(thumbPos.left, thumbPos.top + 200 - w.height())
      else if (thumbPos.top < scrollTop)
        scrollTo(thumbPos.left, thumbPos.top - 100)
    }, 500)
  }
}

function extractExif(element) {
  var exif = $(element).data('exif')
  if (!exif) return null
  exif = exif.split(':')
  return {aperture: exif[0], shutter: exif[1], iso: exif[2], focal: exif[3]}
}
