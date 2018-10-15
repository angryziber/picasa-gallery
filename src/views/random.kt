package views

import photos.Photo
import photos.RandomPhotos

//language=HTML
fun random(random: RandomPhotos, delayMs: String?, refresh: Boolean) = """
<!DOCTYPE html>
<html>
<head>
  <title>Random photo by ${random.author} from ${random.album}</title>
  <style type="text/css">$css</style>
</head>
<body>
<div id="img" style="background-image: url(${random.photos[0].url})"></div>
<div id="title">
  <b>${random.album}</b>
  <div id="description">${random.photos[0].description}</div>
  <div>${random.author}</div>
</div>
</body>
<script>
  var img = document.getElementById('img');
  img.style.display = 'none';
  window.onload = function() {
    img.style.display = 'block';
  };
</script>
<script src="/js/chromecast.js"></script>
<script>
  chromecast.send('${random.photos[0].url}');
  ${if(random.photos.size > 1) """
    var photos = [${random.photos.toJson()}];
    photos.pop();
    var index = 1;
    new Image().src = photos[index].url;
    var desc = document.getElementById('description');

    setInterval(function() {
      ${if (refresh) """if (index == 0) { location.reload(); return; }""" else ""}
      var url = photos[index].url;
      img.style.backgroundImage = 'url(' + url + ')';
      chromecast.send(url);
      desc.innerHTML = photos[index].description;
      if (++index >= photos.length) index = 0;
      new Image().src = photos[index].url;
    }, ${delayMs ?: 8000});
  """ else ""}
</script>
</html>
"""

//language=CSS
private val css = """
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

private fun List<Photo>.toJson() = joinToString {
  """{url:'${it.url}', description:'${it.description?.replace(newline, "")}'}"""
}

private val newline = "\r?\n".toRegex()