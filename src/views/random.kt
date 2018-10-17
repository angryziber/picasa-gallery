package views

import photos.Photo
import photos.RandomPhotos

//language=HTML
fun random(random: RandomPhotos, delayMs: String?, refresh: Boolean) = """
<!DOCTYPE html>
<html>
<head>
  <title>Random photo by ${+random.author} from ${+random.album}</title>
  <style type="text/css">$css</style>
</head>
<body>
<div id="img" style="background-image: url(${+random.photos[0].url})"></div>
<div id="title">
  <b>${+random.album}</b>
  <div id="description">${+random.photos[0].description}</div>
  <div>${+random.author}</div>
</div>
<script>
  var img = document.getElementById('img')
  img.style.display = 'none'
  window.onload = function() {
    img.style.display = 'block'
  }
</script>
<script src="/js/chromecast.js"></script>
<script>
  chromecast.send('${+random.photos[0].url}')
  ${(random.photos.size > 1) / morePhotosJS(random.photos, delayMs, refresh)}
</script>
</body>
</html>
"""

//language=CSS
private const val css = """
  html, body {
    height: 100%;
    background: black;
    margin: 0;
    font-family: sans-serif;
  }
  #img {
    width: 100%;
    height: 100%;
    background: no-repeat center;
    background-size: contain;
    transition: background-image 0.5s;
  }
  #title {
    position: absolute;
    bottom: 25px;
    right: 40px;
    color: white;
    text-shadow: 0 0 10px black;
    padding: 3px 5px;
    text-align: right;
  }
"""

//language=JavaScript
private fun morePhotosJS(photos: List<Photo>, delayMs: String?, refresh: Boolean) = """
  var photos = [${photos.toJson()}]
  var index = 1
  new Image().src = photos[index].url
  var desc = document.getElementById('description')

  setInterval(function() {
    ${refresh / """if (index == 0) { location.reload(); return }"""}
    var url = photos[index].url
    img.style.backgroundImage = 'url(' + url + ')'
    chromecast.send(url)
    desc.innerHTML = photos[index].description
    if (++index >= photos.length) index = 0
    new Image().src = photos[index].url
  }, ${delayMs ?: 8000})
"""

private fun List<Photo>.toJson() = each { """
  {url:'${url.escapeJS()}', description:'${description?.replace(newline, " ")?.escapeJS()}'}"""
}
