<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="random" scope="request" type="net.azib.photos.Picasa.RandomPhoto"/>

<!DOCTYPE html>
<html>
<head>
  <title>Random photo by ${random.nickname} from ${random.album}</title>
  <style type="text/css">
    html, body {
      height: 100%;
      background: black;
      margin: 0;
      text-align: center;
    }
    #img {
      width: 100%;
      height: 100%;
      background-position: center center;
      background-repeat: no-repeat;
      background-size: contain;
    }
    #title {
      position: absolute;
      bottom: 10px;
      right: 15px;
      color: white;
      padding: 3px 5px;
      background: rgba(0, 0, 0, 0.3);
      border-radius: 4px;
      font-family: sans-serif;
      text-align: right;
    }
  </style>
  <script type="text/javascript">
    var img = new Image();
    img.onload = function () {
      document.getElementById('img').style.backgroundImage = 'url(' + img.src + ')';
    };
    img.src = '${random.photo.content.uri}';
  </script>
</head>
<body>
<div id="img"></div>
<div id="title">
  <b>${random.album}</b><br>
  ${random.nickname}
</div>
</body>
</html>
