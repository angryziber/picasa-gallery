if (history.replaceState) history.replaceState(location.pathname, window.title, location.pathname);
window.onpopstate = function(event) {
    console.log('pop ' + event.state);
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
    if (!history.pushState) return true;

    history.pushState(href, href, href);
    onStateChange(href);
    return false;
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
