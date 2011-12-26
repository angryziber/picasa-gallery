function transitionTo(href) {
    if (!history.pushState) return true;

    history.pushState(href, href, href);

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
    return false;
}

function updatePhotoURL(e) {
    var id = e.link.id;
    var album = location.pathname.split('/')[1];
    var url = '/' + album + '/' + id;
    if (history.pushState)
        history.pushState(url, e.link.title, url);
}

function returnAlbumURL() {
    var album = location.pathname.split('/')[1];
    var url = '/' + album;
    if (history.pushState)
        history.pushState(url, '', url);
}

Shadowbox.init({
    continuous: true,
    overlayOpacity: 0.8,
    viewportPadding: 5,
    onOpen: updatePhotoURL,
    onChange: updatePhotoURL,
    onClose: returnAlbumURL
});

if (history.replaceState) history.replaceState(location.pathname, window.title, location.pathname);
window.onpopstate = function(event) {
    if (event.state) transitionTo(event.state);
};