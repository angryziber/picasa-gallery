var chromecast = (function(self) {
  if (navigator.userAgent.indexOf('Chrome') >= 0 && navigator.userAgent.indexOf('CrKey') < 0) {
    window.__onGCastApiAvailable = function(loaded, error) {
      if (loaded) self.init()
      else onerror(error)
    }
    var s = document.createElement('script')
    s.src = '//www.gstatic.com/cv/js/sender/v1/cast_sender.js'; s.async = true
    document.body.appendChild(s)
  }

  var messageCallback
  var nop = function() {}
  var onerror = function(e) {console.log(e)}
  var queue = []

  self.init = function() {
    self.appId = self.appId || chrome.cast.media.DEFAULT_MEDIA_RECEIVER_APP_ID
    self.namespace = self.namespace || 'urn:x-cast:message'
    var sessionRequest = new chrome.cast.SessionRequest(self.appId)
    var apiConfig = new chrome.cast.ApiConfig(sessionRequest, sessionListener, receiverListener)
    chrome.cast.initialize(apiConfig, onerror)
  }

  self.launch = function() {
    if (!self.session) chrome.cast.requestSession(sessionListener)
  }

  self.stop = function(callback) {
    if (self.session) {
      self.session.stop(callback, onerror)
      self.session = null
    }
  }

  self.send = function(url, callback) {
    if (self.session) loadMedia(url, callback)
    else queue.push([url, callback])
  }

  self.message = function(message, callback) {
    self.session.sendMessage(self.namespace, message, callback || nop, onerror)
  }

  self.onMessage = function(callback) {
    messageCallback = function(ns, message) {
      callback(message)
    }
    if (self.session) {
      sessionListener(self.session)
      messageCallback = null
    }
  }

  function sessionListener(session) {
    self.session = session
    if (messageCallback) self.session.addMessageListener(self.namespace, messageCallback)
    if (queue.length) loadMedia.apply(self, queue.pop())
  }

  function receiverListener(e) {
    //if (e === chrome.cast.ReceiverAvailability.AVAILABLE) self.launch()
  }

  function loadMedia(url, callback) {
    var mediaInfo = new chrome.cast.media.MediaInfo(url)
    mediaInfo.metadata = new chrome.cast.media.PhotoMediaMetadata()
    mediaInfo.metadata.metadataType = chrome.cast.media.MetadataType.PHOTO
    mediaInfo.contentType = 'image/jpg'

    var request = new chrome.cast.media.LoadRequest(mediaInfo)
    request.autoplay = true
    request.currentTime = 0
    self.session.loadMedia(request, callback || nop, onerror)
  }

  return self
})(chromecast || {})
