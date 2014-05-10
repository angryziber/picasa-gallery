var chromecast = new (function() {
  if (navigator.userAgent.indexOf('Chrome') >= 0 && navigator.userAgent.indexOf('CrKey') < 0)
    document.write('<script type="text/javascript" src="https://www.gstatic.com/cv/js/sender/v1/cast_sender.js" async></script>');

  window['__onGCastApiAvailable'] = function(loaded, error) {
    if (loaded) init();
    else console.log(error);
  };

  var session;
  var receiverAvailable = false;
  var nop = function() {};
  var queue = [];

  this.send = function(url) {
    if (session) loadMedia(url);
    else queue.push(url);
  };

  function init() {
    var sessionRequest = new chrome.cast.SessionRequest(chrome.cast.media.DEFAULT_MEDIA_RECEIVER_APP_ID);

    var sessionListener = function(s) {
      session = s;
      if (queue.length) loadMedia(queue.pop());
    };

    var receiverListener = function(e) {
      if (e === chrome.cast.ReceiverAvailability.AVAILABLE) {
        receiverAvailable = true;
        setTimeout(function() {
          if (!session) {
            chrome.cast.requestSession(sessionListener);
          }
        }, 1000);
      }
    };

    var apiConfig = new chrome.cast.ApiConfig(sessionRequest, sessionListener, receiverListener);
    chrome.cast.initialize(apiConfig, nop, console.log);
  }

  function loadMedia(url) {
    var mediaInfo = new chrome.cast.media.MediaInfo(url);
    mediaInfo.metadata = new chrome.cast.media.PhotoMediaMetadata();
    mediaInfo.metadata.metadataType = chrome.cast.media.MetadataType.PHOTO;
    mediaInfo.contentType = 'image/jpg';

    var request = new chrome.cast.media.LoadRequest(mediaInfo);
    request.autoplay = true;
    request.currentTime = 0;
    session.loadMedia(request, nop, console.log);
  }
})();
