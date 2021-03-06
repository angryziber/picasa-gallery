package views

// language=HTML
fun sharing() = """
<div id="sharing">
  <a href="https://www.facebook.com/sharer/sharer.php?u=" title="Share on Facebook" class="facebook"></a>
  <a href="https://twitter.com/intent/tweet?url=" title="Share on Twitter" class="twitter"></a>
  <a href="https://pinterest.com/pin/create/button/?media=&description=&url=" title="Share on Pinterest" class="pinterest"></a>
</div>

<script>
  jQuery('#sharing > a').each(function() {
    var url = this.href + encodeURIComponent(location.href.replace(/#.*/, ''))
    url = url.replace('description=', 'description=' + encodeURIComponent(jQuery('h1:first').text().trim()))
    url = url.replace('media=', 'media=' + (jQuery('img.photo').attr('src') || encodeURIComponent(jQuery('[property="og:image"]').attr('content'))))
    this.href = url
  }).on('click', function() {
    var w = 700, h = 500
    var left = (screen.width/2)-(w/2)
    var top = (screen.height/2)-(h/2)
    var url = this.href
    var suffix = location.hash ? location.hash.replace('#', '/') : ''
    if (suffix) url += encodeURIComponent(suffix)
    window.open(url, this.className, 'toolbar=no,status=no,width=' + w + ',height=' + h + ',left=' + left + ',top=' + top)
    if ('ga' in window) ga('send', 'event', 'share-' + this.className, location.pathname + suffix)
    return false
  })
</script>
"""
