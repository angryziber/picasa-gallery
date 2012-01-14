<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="gallery" scope="request" type="com.google.gdata.data.photos.UserFeed"/>

<!DOCTYPE html>
<html>
<head>
    <title>${gallery.nickname} Photography</title>
    <meta name="viewport" content="width=700, intitial-scale=1.0, user-scalable=no">
    <meta name="description" content="${gallery.nickname} photo albums from around the World">
    <meta name="keywords" content="${gallery.nickname},photos,picasa,picasaweb,gallery,albums,travel,photography">
    <meta property="og:title" content="${gallery.nickname} Photography">
    <meta property="og:type" content="website">
    <meta property="og:image" content="${gallery.albumEntries[0].mediaThumbnails[0].url}">
    <meta property="og:site_name" content="${gallery.nickname} Photography">
    <%@include file="head.jsp"%>
    <script type="text/javascript">
        $(initMap);
    </script>
</head>
<body style="background:black">
<div id="header">
    <a href="http://picasaweb.google.com/${gallery.username}" class="button"><img src="/img/picasa-logo.png">Picasaweb<span></span></a>
    <h1 id="title">${gallery.nickname} Photography</h1>
    <form id="search"><input></form>
</div>
<div id="content">
    <div id="map"></div>
    <ul class="albums">
        <c:forEach var="album" items="${gallery.albumEntries}">
            <c:if test="${album.photosUsed > 0}">
                <li>
                    <a id="${album.gphotoId}" class="fade" href="/${album.name}${picasa.urlSuffix}"
                        <c:if test="${album.geoLocation != null}">coords="${album.geoLocation.latitude}:${album.geoLocation.longitude}"</c:if>>
                        <img src="${album.mediaThumbnails[0].url}">
                        <div class="title">
                            <span class="info">${album.photosUsed}</span>
                            <span class="text">${album.title.plainText}</span>
                        </div>
                    </a>
                </li>
            </c:if>
        </c:forEach>
    </ul>

    <div id="footer">
        Photos by <a id="m" href="${gallery.username}">${gallery.nickname}</a>. All rights reserved.
        <br>
        Rendered by <a href="http://github.com/angryziber/picasa-gallery">Picasa Gallery</a>.
        View your <a href="javascript:changeUsername('${gallery.username}')">own gallery</a>.
    </div>
</div>
</body>
</html>