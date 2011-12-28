var ajaxNavigation = history.replaceState && !navigator.appVersion.match(/Mobile/);

if (ajaxNavigation) history.replaceState(location.href, window.title, location.href);
window.onpopstate = function(event) {
    if (event.state) onStateChange(event.state);
};

function onStateChange(href) {
    if (photoViewer.isOpen()) {
        photoViewer.close();
        return;
    }

    $('#content').fadeOut();
    loadingReady = false;
    setTimeout(function() {
        if (!loadingReady)
            $('#content').empty().append('<div style="text-align: center"><img src="/img/loading.gif">Loading...</div>').show();
    }, 2000);

    $.get(href, function(html) {
        loadingReady = true;
        html = $(html);
        var header = html.filter('#header');
        document.title = header.find('#title').text();
        $('#header').replaceWith(header);

        var content = html.filter('#content');
        content.hide();
        $('#content').replaceWith(content);
        updateLayout();
        content.fadeIn();
        photoViewer.setup();
    });
}

function transitionTo(href) {
    if (!ajaxNavigation) return true;

    history.pushState(href, href, href);
    onStateChange(href);
    return false;
}

function goto(href) {
    if (ajaxNavigation) transitionTo(href);
    else location.href = href;
}

function stateURL(photo) {
    var album = location.pathname.split('/')[1];
    return '/' + album + (photo ? '/' + photo.id : '');
}

function loadVisibleThumbs() {
	var visibleTop = $(window).scrollTop() - 150;
    var visibleBottom = visibleTop + $(window).height() + 300;

    var found;
    $('img.missing').each(function() {
        var img = $(this);
        var top = img.offset().top;
        if (top >= visibleTop && top <= visibleBottom) {
            img.attr('src', img.attr('rel'));
            img.removeClass('missing');
            found = true;
        }
        else if (found) return false;
    });
}

function PhotoViewer() {
    var pv = this;
    var w = $(window);
    var wrapper, title;
    var photos = [];
    var index = 0;
    var isOpen = false;

    var pub = {
        setup: function() {
            photos = [];
            $('a.photo').click(pv.open).each(function() {
                var dim = this.rel.split('x');
                photos.push({href: this.href, width: dim[0], height: dim[1], title: this.title, id: this.id});
            });

            wrapper = $('#photo-wrapper');
            if (!wrapper.length)
                wrapper = $('<div id="photo-wrapper"><div class="title"></div></div>').appendTo($('body'));

            title = wrapper.find('.title');
            title.hover(function() {
                title.fadeOut();
            });
        },

        isOpen: function() {
            return isOpen;
        },

        open: function(e) {
            e.preventDefault();
            isOpen = true;
            onResize();
            $(document).keydown(onKeydown);
            $(window).resize(onResize);
            index = $('a.photo').index(this);
            wrapper.find('img').remove();
            wrapper.fadeIn();
            wrapper.click(onMouseClick);
            wrapper.mousemove(onMouseMove);

            loadPhoto();

            var photo = photos[index];
            if (history.pushState) history.pushState(stateURL(photo), photo.title, stateURL(photo));
            wrapper.touchwipe({
                wipeLeft: pub.next,
                wipeRight: pub.previous
            });
        },

        close: function() {
            isOpen = false;
            wrapper.fadeOut();
            $(document).unbind('keydown', onKeydown);
            $(window).unbind('resize', onResize);

            if (history.replaceState) history.replaceState(stateURL(), '', stateURL());
            wrapper.find('img').remove();
        },

        next: function() {
            index++;
            if (index >= photos.length) index = 0;
            loadPhoto();
        },

        prev: function() {
            index--;
            if (index < 0) index = photos.length-1;
            loadPhoto();
        },

        first: function() {
            index = 0;
            loadPhoto();
        },

        last: function() {
            index = photos.length-1;
            loadPhoto();
        }
    };
    $.each(pub, function(name, fun) {pv[name] = fun});

    function posAction(x, y) {
        var img = wrapper.find('img');
        var left = img.offset().left;
        var right = left + img.width();
        var delta = img.width() / 4;
        if (x >= left-20 && x <= left + delta) return pv.prev;
        else if (x >= right - delta && x <= right+20) return pv.next;
        else return pv.close;
    }

    var lastMousePos;
    function onMouseMove(e) {
        var newMousePos = e.pageX + ":" + e.pageY;
        if (lastMousePos != newMousePos) {
            var action = posAction(e.pageX, e.pageY);
            var cursor = action == pv.prev ? 'w-resize' : action == pv.next ? 'e-resize' : 'default';
            wrapper.css('cursor', cursor);
        }
        lastMousePos = newMousePos;
    }

    function onMouseClick(e) {
        e.preventDefault();
        posAction(e.pageX, e.pageY)();
    }

    function onKeydown(e) {
        switch (e.which) {
            case 27: pv.close(); break;
            case 32:
            case 34:
            case 40:
            case 39: pv.next(); e.preventDefault(); break;
            case 8:
            case 33:
            case 38:
            case 37: pv.prev(); e.preventDefault(); break;
            case 36: pv.first(); e.preventDefault(); break;
            case 35: pv.last(); e.preventDefault(); break;
        }
    }

    function onResize() {
        wrapper.width(w.width()).height(w.height()).offset({left: w.scrollLeft(), top: w.scrollTop()});
        centerImage();
        centerTitle();
    }

    function centerImage(img) {
        if (!img) img = wrapper.find('img');
        if (!img.length) return;

        var photo = photos[index];
        var ww = wrapper.width(), wh = wrapper.height();
        if (photo.width > ww || photo.height > wh) {
            if (ww / wh > photo.width / photo.height)
                img.attr('height', wh);
            else
                img.attr('width', ww);
        }
        img.offset({left: (w.width()-img.width())/2, top: (w.height()-img.height())/2});
    }

    function centerTitle() {
        title.offset({left: Math.max(0, (w.width() - title.width()) / 2)});
    }

    function imageOnLoad() {
        var img = $(this);
        wrapper.append(img);
        centerImage(img);
        img.fadeIn();
        wrapper.css('cursor', 'none');

        // preload next image
        if (index < photos.length-1)
            setTimeout(function() {
                var tmp = new Image();
                tmp.src = photos[index+1].href;
            }, 100);
    }

    function loadPhoto() {
        wrapper.css('cursor', 'wait');
        wrapper.find('img').fadeOut(function() {
            $(this).remove();
        });

        var photo = photos[index];
        var newImg = new Image();
        newImg.onload = imageOnLoad;
        newImg.style.display = 'none';
        newImg.src = photo.href;

        title.text(photo.title);
        centerTitle();
        if (photo.title) title.fadeIn(); else title.fadeOut();

        if (history.replaceState) history.replaceState(stateURL(photo), photo.title, stateURL(photo));
    }
}

function doSearch() {
    goto('/' + $('#search').val());
    return false;
}

var markers = [];
var map, bounds;
function latLng(lat, lon) {
    return new google.maps.LatLng(lat, lon);
}
function initMap() {
    bounds = new google.maps.LatLngBounds();
    map = new google.maps.Map($('#map')[0], {
        mapTypeId: google.maps.MapTypeId.TERRAIN,
        styles: [{
            stylers: [
              { saturation: -5 },
              { gamma: 0.38 },
              { lightness: -33 }
            ]
        }],
        streetViewControl: false,
        zoomControl: false,
        panControl: false,
        minZoom: 1
    });
    for (var i in markers) {
        var marker = new google.maps.Marker({position: markers[i].pos, map: map, title: markers[i].title});
        bounds.extend(markers[i].pos);
        function listen(i) {
            google.maps.event.addListener(marker, 'click', function() {
                $('.albums a').eq(i).click();
            });
        }
        listen(i);
        markers[i].marker = marker;
    }
    map.fitBounds(bounds);
    map.panBy(0, 15);
}

function updateLayout() {
    var photoWidth = ($('.albums').length ? 218 : 150) + 10;
    var photosInRow = Math.floor($(window).width() / photoWidth);
    $('#content').width(photosInRow * photoWidth);
    loadVisibleThumbs();
    if ($('#map').length) {
        setTimeout(initMap, 300);
    }
}

var photoViewer = new PhotoViewer();

$(function() {
    updateLayout();
    photoViewer.setup();
    $(window).resize(updateLayout);
    $(window).scroll(loadVisibleThumbs);
    $.ajaxSetup({
       error: function(req) {
           if (req.status == 0) return;
           alert('Failed: ' + req.status + ' ' + req.statusText + (req.responseText && req.responseText.length < 200 ? ': ' + req.responseText : ''));
           location.href = '/';
       }
    });
    $('a#m').attr('href', 'm' + 'ail' + 'to:' + $('a#m').attr('href') + String.fromCharCode(64) + 'gmail.com');
});

(function($) {
    $.fn.touchwipe = function(settings) {
        var config = {
            min_move_x: 20,
            min_move_y: 20,
            wipeLeft: function() {},
            wipeRight: function() {},
            wipeUp: function() {},
            wipeDown: function() {},
            preventDefaultEvents: true
        };

        if (settings) $.extend(config, settings);

        this.each(function() {
            var startX;
            var startY;
            var isMoving = false;

            function cancelTouch() {
                this.removeEventListener('touchmove', onTouchMove);
                startX = null;
                isMoving = false;
            }

            function onTouchMove(e) {
                if (config.preventDefaultEvents) {
                    e.preventDefault();
                }
                if (isMoving) {
                    var x = e.touches[0].pageX;
                    var y = e.touches[0].pageY;
                    var dx = startX - x;
                    var dy = startY - y;
                    if (Math.abs(dx) >= config.min_move_x) {
                        cancelTouch();
                        if (dx > 0)
                            config.wipeLeft();
                        else
                            config.wipeRight();
                        isMoving = false;
                    }
                    else if (Math.abs(dy) >= config.min_move_y) {
                        cancelTouch();
                        if (dy > 0)
                            config.wipeDown();
                        else
                            config.wipeUp();
                        isMoving = false;
                    }
                }
            }

            function onTouchStart(e) {
                if (e.touches.length == 1) {
                    startX = e.touches[0].pageX;
                    startY = e.touches[0].pageY;
                    isMoving = true;
                    this.addEventListener('touchmove', onTouchMove, false);
                }
            }

            if ('ontouchstart' in document.documentElement)
                this.addEventListener('touchstart', onTouchStart, false);
        });

        return this;
    };
})(jQuery);