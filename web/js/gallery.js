function changeUsername(username) {
  username = prompt('Show photos by Google/Picasaweb user:', username)
  if (username) fadeTo('/?by=' + username)
}

function PhotoViewer() {
  var pub = this
  var w = $(window)
  var wrapper, title, map, marker, controls, position, interval, timeRemaining, exif
  var slideshow = null
  var photos = []
  var index = 0
  var isOpen = false

  pub.setup = function() {
    photos = []
    $('a.photo').click(pub.open).each(function() {
      var title = $('img', this).attr('alt')
      photos.push({
        url: this.href,
        title: title,
        id: this.id,
        pos: extractPos(this),
        exif: extractExif(this),
        time: this.dataset.time
      })
      this.href = this.dataset.href
      if (title) this.setAttribute('title', title)
    })

    wrapper = $('#photo-wrapper')
    wrapper.unbind()
    wrapper.click(onMouseClick)
    wrapper.mousemove(onMouseMove)
    controls = wrapper.find('#photo-controls')
    controls.find('#inc-interval').click(incInterval)
    controls.find('#dec-interval').click(decInterval)
    position = controls.find('#position')
    interval = controls.find('#interval')
    timeRemaining = controls.find('#time-remaining')
    exif = wrapper.find('#photo-exif')

    title = wrapper.find('.title')
    title.hover(function() {
      title.fadeOut()
    })
    return this
  }

  pub.isOpen = function() {
    return isOpen
  }

  pub.open = function() {
    if (isOpen) return false
    isOpen = true
    onResize()
    $(document).keydown(onKeydown)
    $(window).resize(onResize)
    window.onpopstate = onPopState
    wrapper[0].ontouchstart = onTouchStart
    wrapper[0].ontouchmove = onTouchMove

    index = $('a.photo').index(this)
    wrapper.find('img.photo').remove()
    wrapper.fadeIn()
    setTimeout(function() {
      controls.removeClass('visible')
    }, 2000)
    requestFullScreen()

    onHashChange()
    $(window).bind('hashchange', onHashChange)
    loadPhoto()
    var photo = photos[index]
    if (history.pushState) history.pushState(stateURL(photo), photo.title, stateURL(photo))
    return false
  }

  pub.close = function() {
    isOpen = false
    wrapper.fadeOut(function() {
      controls.addClass('visible')
    })
    $(document).unbind('keydown')
    $(window).unbind('resize')
    $(window).unbind('hashchange')
    window.onpopstate = $.noop

    stopSlideshow()
    if (history.replaceState) history.replaceState(stateURL(), '', stateURL())
    var img = wrapper.find('img.photo')
    var thumb = $('#' + photos[index].id)
    var fixed = wrapper.css('position') == 'fixed'
    img.animate({
      top: thumb.offset().top - (fixed ? w.scrollTop() : 0),
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

  function requestFullScreen() {
    if ($('meta[name=viewport]').length) return // fullscreen breaks viewport on mobile devices

    var el = document.documentElement
    var rfs = (el.requestFullscreen || el.webkitRequestFullscreen || el.mozRequestFullScreen || el.msRequestFullscreen)
    if (rfs) rfs.call(el)

    var fschange = 'fullscreenchange webkitfullscreenchange mozfullscreenchange msfullscreenchange'
    $(document).off(fschange).on(fschange, function() {
      var fs = document.fullscreenElement || document.webkitFullscreenElement || document.mozFullScreenElement || document.msFullscreenElement
      if (!fs && !slideshow && !(chromecast || {}).session)
        pub.close()
    })
  }

  function onHashChange() {
    var hash = location.hash.substring(1)
    if (hash == 'slideshow') startSlideshow()
    else if (slideshow) stopSlideshow()
  }

  function stateURL(photo) {
    return photo ? '#' + photo.id : slideshow ? '#slidehow' : location.href.replace(/#.*/, '')
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
    controls.find('#slideshow.button').text('Stop').attr('href', '#')
    showTimeRemaining()
  }

  function stopSlideshow() {
    clearTimeout(slideshow)
    slideshow = null
    controls.find('#slideshow.button').text('Slideshow').attr('href', '#slideshow')
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

  function onPopState(event) {
    if (event.state) pub.close()
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

  function onKeydown(e) {
    switch (e.which) {
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
    if (window.innerHeight) wrapper.height(window.innerHeight) // iPhone workaround, http://bugs.jquery.com/ticket/6724, height:100% doesn't work if address bar is hidden
    if (wrapper.css('position') == 'absolute') { // fixed not supported on eg, Android and iOS < 4
      wrapper.width(w.width()).css('top', w.scrollTop())
    }
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
    var photo = photos[index]
    photo.width = this.width
    photo.height = this.height
    var img = $(this)
    wrapper.append(img)
    centerImage(img)
    img.fadeIn()
    wrapper.css('cursor', 'none')

    if (slideshow)
      setSlideshowTimeout()

    // preload next image
    if (index < photos.length - 1)
      setTimeout(function() {
        var tmp = new Image()
        tmp.src = photos[index + 1].url
      }, 100)
  }

  function loadPhoto() {
    wrapper.css('cursor', 'wait')
    wrapper.find('img.photo').fadeOut(function() {
      $(this).remove()
    })

    var photo = photos[index]
    var newImg = new Image()
    newImg.className = 'photo'
    newImg.style.display = 'none'
    newImg.onload = imageOnLoad
    newImg.src = photo.url
    if ('chromecast' in window) chromecast.send(photo.url)

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

    if (photo.pos) {
      if (!map) {
        map = createMap('#photo-map', {mapTypeControl: false})
        map.setCenter(photo.pos)
        map.setZoom(12)
        marker = new google.maps.Marker({position: photo.pos, map: map})
        //$('#photo-map').hover(function(){setTimeout(function(){map.panTo(photos[index].pos)}, 500)})
      }
      marker.setPosition(photo.pos)
      if (map.getBounds() && !map.getBounds().contains(photo.pos)) map.panTo(photo.pos)
      $('#photo-map').fadeIn()
    }
    else {
      $('#photo-map').fadeOut()
    }

    updateScrolling(photo)
  }

  function updateScrolling(photo) {
    if (wrapper.css('position') != 'fixed') return

    setTimeout(function() {
      var thumbPos = $('#' + photo.id).offset()
      var scrollTop = w.scrollTop()
      if (thumbPos.top + 150 > scrollTop + w.height())
        scrollTo(thumbPos.left, thumbPos.top + 200 - w.height())
      else if (thumbPos.top < scrollTop)
        scrollTo(thumbPos.left, thumbPos.top - 100)
    }, 1000)
  }
}

function latLng(lat, lon) {
  return window.google ? new google.maps.LatLng(lat, lon) : null
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
    minZoom: 1
  }
  $.extend(options, moreOpts)
  return new google.maps.Map($(selector)[0], options)
}

function extractPos(element) {
  var coords = $(element).data('coords')
  if (!coords) return null
  coords = coords.split(':')
  return latLng(coords[0], coords[1])
}

function extractExif(element) {
  var exif = $(element).data('exif')
  if (!exif) return null
  exif = exif.split(':')
  return {aperture: exif[0], shutter: exif[1], iso: exif[2], focal: exif[3]}
}

function setMarkerIcon(marker, name) {
  marker.setIcon('https://maps.google.com/mapfiles/' + (name || 'marker') + '.png')
  marker.setZIndex(1000)
}

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

function fadeTo(href) {
  $('#content').fadeOut(function() {
    location.href = href
  })
}

$(function() {
  setTimeout(function() {scrollTo(0, 1)}, 10)
  $('#content').fadeIn(1000)

  $('a.fade').click(function() {
    fadeTo(this.href)
    return false
  })

  $('form#search').submit(function() {
    fadeTo('/?q=' + $(this).find('input').val() + location.search.replace(/\?q=.*?(&|$)/, '&'))
    return false
  })
})
