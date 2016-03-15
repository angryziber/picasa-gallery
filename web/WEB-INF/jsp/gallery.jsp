<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="gallery" scope="request" type="com.google.gdata.data.photos.UserFeed"/>

<!DOCTYPE html>
<html>
<head>
  <title>${gallery.nickname} Photography</title>
  <c:if test="${mobile}">
    <meta name="viewport" content="width=700, user-scalable=no">
  </c:if>
  <meta name="description" content="${gallery.nickname} photo albums from around the World">
  <meta name="keywords" content="${gallery.nickname},photos,picasa,picasaweb,gallery,albums,travel,photography,<c:forEach var="album" items="${gallery.albumEntries}">${album.title.plainText},</c:forEach>">
  <meta property="og:title" content="${gallery.nickname} Photography">
  <meta property="og:type" content="website">
  <meta property="og:image" content="${gallery.albumEntries[0].mediaThumbnails[0].url}">
  <meta property="og:site_name" content="${gallery.nickname} Photography">
  <%@include file="head.jsp" %>
  <script type="text/javascript">
    $(initMap);
    $(initAlbumFilter);
  </script>
</head>

<body style="background:black; color: gray">

<div id="header" class="header">
  <a href="http://picasaweb.google.com/${gallery.username}" class="button" title="View the gallery in Picasaweb/Google+"><img src="/img/picasa-logo.png">Picasaweb</a>

  <form id="search"><input type="text" placeholder="Filter or Search" title="Type to filter albums or press enter to search for individual photos"></form>
  <h1 id="title">${gallery.nickname} Photography</h1>
</div>

<div id="content">
  <div id="map"></div>
  <div class="albums">
    <c:forEach var="album" items="${gallery.albumEntries}">
      <c:if test="${album.photosUsed > 1}">
        <a id="${album.gphotoId}" class="fade" href="/${album.name}${picasa.urlSuffix}" <c:if test="${album.geoLocation != null}">data-coords="${album.geoLocation.latitude}:${album.geoLocation.longitude}"</c:if>>
          <img src="${album.mediaThumbnails[0].url}" alt="${album.title.plainText} photos" title="${album.description.plainText}">

          <div class="title">
            <span class="info">${album.photosUsed}</span>
            <span class="text">${album.title.plainText}</span>
            <span class="description hidden">${album.description.plainText}</span>
          </div>
        </a>
      </c:if>
    </c:forEach>
  </div>

  <div id="footer">
    Photos by <a id="m" href="${picasa.user}">${gallery.nickname}</a>. All rights reserved. <a href="?random${fn:replace(picasa.urlSuffix, '?', '&')}">Random photo</a>.
    <br>
    Rendered by <a href="http://github.com/angryziber/picasa-gallery">Picasa Gallery</a>.
    View your <a href="javascript:changeUsername('${picasa.user}')">own gallery</a>.
  </div>
</div>

<h2 class="hidden">${gallery.nickname} <b>photos</b> and pictures</h2>
</body>
</html>