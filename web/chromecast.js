var chromecast = new (function() {
  var self = this;
  self.appId = undefined;
  self.namespace = 'urn:x-cast:message';

  if (navigator.userAgent.indexOf('Chrome') >= 0 && navigator.userAgent.indexOf('CrKey') < 0) {
    document.write('<script type="text/javascript" src="//www.gstatic.com/cv/js/sender/v1/cast_sender.js" async></script>');

    window['__onGCastApiAvailable'] = function(loaded, error) {
      if (loaded) self.init(self.appId || chrome.cast.media.DEFAULT_MEDIA_RECEIVER_APP_ID);
      else console.log(error);
    };
  }

  var session;
  var messageCallback;
  var receiverAvailable = false;
  var nop = function() {};
  var onerror = function(e) {console.log(e)};
  var queue = [];

  self.send = function(url, callback) {
    if (session) loadMedia(url, callback);
    else queue.push([url, callback]);
  };

  self.message = function(message, callback) {
    session.sendMessage(self.namespace, message, callback || nop, onerror);
  };

  self.onMessage = function(callback) {
    messageCallback = function(ns, message) {
      callback(message);
    };
    if (session) {
      session.addMessageListener(self.namespace, messageCallback);
      messageCallback = null;
    }
  };

  self.init = function(appId, callback) {
    var sessionRequest = new chrome.cast.SessionRequest(appId);
    var apiConfig = new chrome.cast.ApiConfig(sessionRequest, sessionListener, receiverListener);
    chrome.cast.initialize(apiConfig, callback || nop, onerror);
  };

  self.launch = function() {
    if (!session) {
      chrome.cast.requestSession(sessionListener);
    }
  };

  self.stop = function(callback) {
    if (session) {
      session.stop(callback, onerror);
      session = null;
    }
  };

  function sessionListener(s) {
    session = s;
    if (messageCallback) session.addMessageListener(self.namespace, messageCallback);
    if (queue.length) loadMedia.apply(self, queue.pop());
  }

  function receiverListener(e) {
    if (e === chrome.cast.ReceiverAvailability.AVAILABLE) {
      receiverAvailable = true;
    }
  }

  function loadMedia(url, callback) {
    var mediaInfo = new chrome.cast.media.MediaInfo(url);
    mediaInfo.metadata = new chrome.cast.media.PhotoMediaMetadata();
    mediaInfo.metadata.metadataType = chrome.cast.media.MetadataType.PHOTO;
    mediaInfo.contentType = 'image/jpg';

    var request = new chrome.cast.media.LoadRequest(mediaInfo);
    request.autoplay = true;
    request.currentTime = 0;
    session.loadMedia(request, callback || nop, onerror);
  }
})();
