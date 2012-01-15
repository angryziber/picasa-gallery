<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="gallery" scope="request" type="com.google.gdata.data.photos.UserFeed"/>

<!DOCTYPE html>
<html>
<head>
    <title>${gallery.nickname} Photography</title>
    <meta name="viewport" content="width=700, user-scalable=no">
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

<body style="background:black; color: gray">

<div id="header" class="header">
    <a href="http://picasaweb.google.com/${gallery.username}" class="button"><img src="/img/picasa-logo.png">Picasaweb<span></span></a>
    <h1 id="title">${gallery.nickname} Photography</h1>
    <form id="search"><input></form>
</div>

<div id="content">
    <div id="map"></div>
    <div class="albums">
        <c:forEach var="album" items="${gallery.albumEntries}">
            <c:if test="${album.photosUsed > 0}">
                <a id="${album.gphotoId}" class="fade" href="/${album.name}${picasa.urlSuffix}"
                    <c:if test="${album.geoLocation != null}">coords="${album.geoLocation.latitude}:${album.geoLocation.longitude}"</c:if>>
                    <img src="${album.mediaThumbnails[0].url}" alt="${album.title.plainText} photos, pictures">
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
        Photos by <a id="m" href="${gallery.username}">${gallery.nickname}</a>. All rights reserved.
        <br>
        Rendered by <a href="http://github.com/angryziber/picasa-gallery">Picasa Gallery</a>.
        View your <a href="javascript:changeUsername('${gallery.username}')">own gallery</a>.
    </div>
</div>

<h2 class="hidden">${gallery.nickname} <b>photos</b> and pictures</h2>
</body>
</html>