function transitionTo(href) {
    if (!history.pushState) return true;

    history.pushState(href, href, href);
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

function init() {
    Shadowbox.init({
        continuous: true,
        overlayOpacity: 0.8,
        viewportPadding: 5
    });
}

init();
if (history.replaceState) history.replaceState(location.href, window.title, location.href);

window.onPopState = function(event) {
    if (event.state) transitionTo(event.state);
};