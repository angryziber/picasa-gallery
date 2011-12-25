function transitionTo(href) {
    if (!history.pushState) return true;

    history.pushState(href, href, href);
    $('#content').fadeOut();
    $.get(href, function(html) {
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