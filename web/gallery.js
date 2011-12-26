var ajaxNavigation = history.replaceState && !navigator.appVersion.match(/Mobile/);

if (ajaxNavigation) history.replaceState(location.href, window.title, location.href);
window.onpopstate = function(event) {
    if (event.state) onStateChange(event.state);
};

function onStateChange(href) {
    if (Shadowbox.isOpen()) {
        Shadowbox.close();
        return;
    }

    $('#content').fadeOut();
    loadingReady = false;
    setTimeout(function() {
        if (!loadingReady)
            $('#content').empty().append('<div id="sb-loading"><div id="sb-loading-inner"><span>Loading...</span></div></div>').show();
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
        content.fadeIn();
        Shadowbox.setup();
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

function stateURL(e) {
    var album = location.pathname.split('/')[1];
    return '/' + album + (e ? '/' + e.link.id : '');
}

Shadowbox.init({
    continuous: true,
    overlayOpacity: 0.8,
    viewportPadding: 5,
    onOpen: function(e) {
        if (history.pushState) history.pushState(stateURL(e), e.link.title, stateURL(e));
        $('#sb-container').touchwipe({
            wipeLeft: Shadowbox.next,
            wipeRight: Shadowbox.previous
        });
    },
    onChange: function(e) {
        if (history.replaceState) history.replaceState(stateURL(e), e.link.title, stateURL(e));
    },
    onClose: function() {
        if (history.replaceState) history.replaceState(stateURL(), '', stateURL());
    }
});

$.ajaxSetup({
   error: function(req) {
       if (req.status == 0) return;
       alert('Failed: ' + req.status + ' ' + req.statusText + (req.responseText && req.responseText.length < 200 ? ': ' + req.responseText : ''));
       location.href = '/';
   }
});

function doSearch() {
    goto('/' + $('#search').val());
    return false;
}

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