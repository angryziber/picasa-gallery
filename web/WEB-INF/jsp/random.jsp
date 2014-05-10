<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="random" scope="request" type="net.azib.photos.Picasa.RandomPhotos"/>

<!DOCTYPE html>
<html>
<head>
  <title>Random photo by ${random.nickname} from ${random.album}</title>
  <style type="text/css">
    html, body {
      height: 100%;
      background: black;
      margin: 0;
      font-family: sans-serif;
    }
    #img {
      width: 100%;
      height: 100%;
      background-position: center center;
      background-repeat: no-repeat;
      background-size: contain;
      -webkit-transition: background-image 0.5s;
      -moz-transition: background-image 0.5s;
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
  </style>
</head>
<body>
<div id="img" style="background-image: url(${random.photos[0].content.uri})"></div>
<div id="title">
  <b>${random.album}</b><br>
  ${random.nickname}
</div>
</body>
<script type="text/javascript" src="/chromecast.js"></script>
<script type="text/javascript">
  var img = document.getElementById('img');
  img.style.display = 'none';
  window.onload = function() {
    img.style.display = 'block';
  };
  chromecast.send('${random.photos[0].content.uri}');
  <c:if test="${fn:length(random.photos) > 1}">
    var photos = [<c:forEach var="photo" items="${random.photos}">'${photo.content.uri}', </c:forEach>null];
    photos.pop();
    var index = 1;
    new Image().src = photos[index];

    setInterval(function() {
      <c:if test="${refresh}">if (index == 0) { location.reload(); return; }</c:if>
      var url = photos[index++];
      img.style.backgroundImage = 'url(' + url + ')';
      if (index >= photos.length) index = 0;
      new Image().src = photos[index];
      chromecast.send(url);
    }, ${delay != null ? delay : 8000});
  </c:if>
</script>
</html>
