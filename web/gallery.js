function facebookButton(href) {
    if (!href) href = location.href;
    return '<iframe class="facebook-button" scrolling="no" frameborder="0" allowtransparency="true" ' +
           'src="http://www.facebook.com/plugins/like.php?href=' + href + '&layout=button_count&action=like&width=90&height=20&colorscheme=dark"></iframe>'
}

function stateURL(photo) {
    var album = location.pathname.split('/')[1];
    return '/' + album + (photo ? '/' + photo.id : '') + location.search;
}

function loadVisibleThumbs(maxCount) {
    if (!maxCount) maxCount = 10000;
	var visibleTop = $(window).scrollTop() - 150;
    var visibleBottom = visibleTop + $(window).height() + 300;

    var found = false, count = 0;
    $('img.missing').each(function() {
        var img = $(this);
        var top = img.offset().top;
        if (top >= visibleTop && top <= visibleBottom) {
            img.attr('src', img.attr('rel'));
            img.removeClass('missing');
            found = true;
        }
        else if (found) return false;
        if (++count > maxCount) return false;
    });
}

var markerIcon;

function changeUsername(username) {
    username = prompt('Show photos by Google/Picasaweb user:', username);
    if (username) fadeTo('/?by=' + username);
}

function PhotoViewer() {
    var pub = this;
    var w = $(window);
    var wrapper, internalwrapper, title, map, controls, position, interval, timeRemaining;
    var marker;
    var slideshow = null;
    var photos = [];
    var index = 0;
    var isOpen = false;

    pub.setup = function() {
        photos = [];
        $('a.photo').click(pub.open).each(function() {
            photos.push({href: this.href, title: this.title, id: this.id, pos: extractPos(this)});
        });

        wrapper = $('#photo-wrapper');
        internalwrapper = $('#internal-photo-wrapper');
        wrapper.unbind();
        wrapper.click(onMouseClick);
        wrapper.mousemove(onMouseMove);
        controls = wrapper.find('#photo-controls');
        controls.find('#slideshow').click(pub.slideshow);
        controls.find('#incInterval').click(incInterval);
        controls.find('#decInterval').click(decInterval);
        position = controls.find('#position');
        interval = controls.find('#interval');
        timeRemaining = controls.find('#timeRemaining');

        title = wrapper.find('.title');
        title.hover(function() {
            title.fadeOut();
        });
        return this;
    };

    pub.isOpen = function() {
        return isOpen;
    };

    pub.open = function() {
        if (isOpen) return false;
        isOpen = true;
        onResize();
        $(document).keydown(onKeydown);
        $(window).resize(onResize);
        window.onpopstate = onPopState;
        wrapper[0].ontouchstart = onTouchStart;
        wrapper[0].ontouchmove = onTouchMove;

        index = $('a.photo').index(this);
        internalwrapper.find('img').remove();
        wrapper.fadeIn();
        setTimeout(function() {
            controls.removeClass('visible');
        }, 2000);

        loadPhoto();
        var photo = photos[index];
        if (history.pushState) history.pushState(stateURL(photo), photo.title, stateURL(photo));
        return false;
    };

    pub.close = function() {
        isOpen = false;
        wrapper.fadeOut();
        $(document).unbind('keydown');
        $(window).unbind('resize');
        window.onpopstate = $.noop;

        if (history.replaceState) history.replaceState(stateURL(), '', stateURL());
        internalwrapper.find('img').remove();
        stopSlideshow();
    };

    pub.next = function() {
        index++;
        if (index >= photos.length) index = 0;
        loadPhoto();
    };

    pub.prev = function() {
        index--;
        if (index < 0) index = photos.length-1;
        loadPhoto();
    };

    pub.first = function() {
        index = 0;
        loadPhoto();
    };

    pub.last = function() {
        index = photos.length-1;
        loadPhoto();
    };

    pub.slideshow = function() {
        if (slideshow) stopSlideshow();
        else startSlideshow();
        return false;
    };

    function showTimeRemaining() {
        var sec = (photos.length - index - 1) * interval.text();
        var min = 0;
        if (sec > 60) {
            min = Math.floor(sec / 60);
            sec = sec % 60;
        }
        timeRemaining.text((min ? min + ' min ' : '') + sec + ' sec');
    }

    function setSlideshowTimeout() {
        slideshow = setTimeout(function() {
            pub.next();
            showTimeRemaining();
        }, interval.text() * 1000);
    }

    function startSlideshow() {
        setSlideshowTimeout();
        controls.find('#slideshow.button').html('Stop<span></span>');
        showTimeRemaining();
    }

    function stopSlideshow() {
        clearTimeout(slideshow); slideshow = null;
        controls.find('#slideshow.button').html('Slideshow<span></span>');
        timeRemaining.empty();
    }

    function incInterval() {
        interval.text(parseInt(interval.text())+1);
        if (slideshow) {
            stopSlideshow(); startSlideshow();
        }
        return false;
    }

    function decInterval() {
        interval.text(Math.max(1, parseInt(interval.text())-1));
        if (slideshow) {
            stopSlideshow(); startSlideshow();
        }
        return false;
    }

    function onPopState(event) {
        if (event.state) pub.close();
    }

    function posAction(x, y) {
        var img = internalwrapper.find('img');
        if (!img.length) return pub.close;
        var left = img.offset().left;
        var right = left + img.width();
        var delta = img.width() / 4;
        if (x >= left-20 && x <= left + delta) return pub.prev;
        else if (x >= right - delta && x <= right+20) return pub.next;
        else return pub.close;
    }

    var lastMousePos;
    function onMouseMove(e) {
        var newMousePos = e.pageX + ":" + e.pageY;
        if (lastMousePos != newMousePos) {
            var action = posAction(e.pageX, e.pageY);
            var cursor = action == pub.prev ? 'url(/img/left-cursor.png),w-resize' : action == pub.next ? 'url(/img/right-cursor.png),e-resize' : 'default';
            wrapper.css('cursor', cursor);
        }
        lastMousePos = newMousePos;
    }

    function onMouseClick(e) {
        posAction(e.pageX, e.pageY)();
        return false;
    }

    var touchStartX;
    function onTouchStart(e) {
        touchStartX = e.touches[0].pageX;
    }
    function onTouchMove(e) {
        if (!touchStartX) return false;
        var dx = e.touches[0].pageX - touchStartX;
        if (dx > 20) {
            pub.next();
            touchStartX = null;
        }
        else if (dx < -20) {
            pub.prev();
            touchStartX = null;
        }
        return false;
    }

    function onKeydown(e) {
        switch (e.which) {
            case 27: pub.close(); return false;
            case 32:
            case 34:
            case 40:
            case 39: pub.next(); return false;
            case 8:
            case 33:
            case 38:
            case 37: pub.prev(); return false;
            case 36: pub.first(); return false;
            case 35: pub.last(); return false;
            default: return true;
        }
    }

    function onResize() {
        if (window.innerHeight) wrapper.height(window.innerHeight); // iPhone workaround, http://bugs.jquery.com/ticket/6724, height:100% doesn't work if address bar is hidden
        if (wrapper.css('position') == 'absolute') { // fixed not supported on eg, Android and iOS < 4
            wrapper.width(w.width()).css('top', w.scrollTop());
        }
        centerImage();
        centerTitle();
    }

    function centerImage(img) {
        if (!img) img = internalwrapper.find('img');
        if (!img.length) return;

        var photo = photos[index];
        var ww = wrapper.width(), wh = wrapper.height();
        if (photo.width > ww || photo.height > wh) {
            if (ww / wh > photo.width / photo.height) {
                img.attr('height', wh);
                img.removeAttr('width');
            } else {
                img.attr('width', ww);
                img.removeAttr('height');
            }
        }
        img.css('top', (wrapper.height()-img.height())/2);
        img.css('left', (wrapper.width()-img.width())/2);
    }

    function centerTitle() {
        title.css('width', title.height() > title.css('line-height').replace('px', '') ? w.width()*0.95 : 'auto');
    }

    function imageOnLoad() {
        photos[index].width = this.width;
        photos[index].height = this.height;
        var img = $(this);
        internalwrapper.append(img);
        centerImage(img);
        img.fadeIn();
        wrapper.css('cursor', 'none');

        if (slideshow)
            setSlideshowTimeout();

        // preload next image
        if (index < photos.length-1)
            setTimeout(function() {
                var tmp = new Image();
                tmp.src = photos[index+1].href;
            }, 100);
    }

    function loadPhoto() {
        wrapper.css('cursor', 'wait');
        internalwrapper.find('img').fadeOut(function() {
            $(this).remove();
        });

        var photo = photos[index];
        var newImg = new Image();
        newImg.onload = imageOnLoad;
        newImg.style.display = 'none';
        newImg.src = photo.href;

        title.text(photo.title);
        if (photo.title) title.fadeIn(); else title.fadeOut();
        centerTitle();

        var url = stateURL(photo);
        if (history.replaceState) history.replaceState(url, photo.title, url);
        _gaq.push(['_trackPageview', url]);

        position.text((index+1) + ' of ' + photos.length);

        controls.find('.facebook-button').remove();
        controls.find('.header').prepend(facebookButton('http://' + location.host + stateURL(photo)));

        if (photo.pos) {
            if (!map) {
                map = createMap('photo-map');
                map.setView(photo.pos, 14);
            } else {
                map.setView(photo.pos, 14);
            }
            $('#photo-map').show();
            if (!marker) {
                marker = new L.Marker(photo.pos, {icon: markerIcon});
                map.addLayer(marker);
            } else {
                marker.setLatLng(photo.pos);
            }
        }
        else {
            $('#photo-map').hide();
        }
    }
}

function latLng(lat, lon) {
    return new L.LatLng(lat, lon);
}

function createMap(selector) {
    var map = new L.Map(selector);

    if (!markerIcon) {
        markerIcon = new (L.Icon.extend({
            iconUrl: "/img/marker.png",
            shadowUrl: "/img/marker-shadow.png"
        }))();
    }

    var mapUrl = 'http://otile1.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.jpg';

    var layer = new L.TileLayer(mapUrl, {maxZoom: 18, attribution: ("Map data © <a href='http://osm.org'>OpenStreetMap</a> contributors, CC-BY-SA; rendering by <a href='http://open.mapquestapi.com/'>MapQuest</a>")});
    map.addLayer(layer);

    return map;
}

function extractPos(element) {
    var coords = $(element).attr('coords');
    if (!coords) return null;
    coords = coords.split(':');
    return latLng(coords[0], coords[1]);
}

function initMap() {
    var bounds = new L.LatLngBounds();
    var map = createMap('map');
    $('.albums > a').each(function (i, link) {
        var pos = extractPos(this);
        if (!pos) return;
        this.marker = new L.Marker(pos, {title: $(link).find('.title > .text').text(), icon: markerIcon});
        map.addLayer(this.marker);
        bounds.extend(pos);
        this.marker.on('click', function () {
            $(link).click();
        });
    });

    if (!bounds.getSouthWest()) {
        map.setView(latLng(0, 0), 1);
    } else {
        map.fitBounds(bounds);
        map.panBy(L.Point(0, 15));
    }
}

function updateLayout() {
    var photoWidth = ($('.albums').length ? 218 : 150) + 10;
    var photosInRow = Math.floor($(window).width() / photoWidth);
    var photosInColumn = Math.ceil($(window).height() / photoWidth);
    $('#content').width(photosInRow * photoWidth);
    loadVisibleThumbs(photosInRow * (photosInColumn * 2));
}

function fadeTo(href) {
    $('#content').fadeOut(function() {
        location.href = href;
    });
}

$(function() {
    setTimeout(function() {scrollTo(0, 1)}, 10);
    updateLayout();
    $('#content').fadeIn(1000);

    $('a.fade').click(function() {
        fadeTo(this.href);
        return false;
    });

    $('form#search').submit(function() {
        fadeTo('/' + $(this).find('input').val() + location.search);
        return false;
    });

    $(window).resize(updateLayout);
    $(window).scroll(loadVisibleThumbs);
    $('a#m').attr('href', 'm' + 'ail' + 'to:' + $('a#m').attr('href') + String.fromCharCode(64) + 'gmail.com');
});
