<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="gallery" scope="request" type="com.google.gdata.data.photos.UserFeed"/>

<!DOCTYPE html>
<html>
<head>
    <title>${gallery.nickname} Photography</title>
    <%@include file="head.jsp"%>
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
</head>
<body>
<div id="header">
    <a href="http://picasaweb.google.com/${gallery.username}" class="button"><img src="/img/picasa-logo.png">Picasaweb<span></span></a>
    <h1 id="title">${gallery.nickname} Photography</h1>
    <form onsubmit="return doSearch()"><input id="search"></form>
</div>
<div id="content">
    <div id="map"></div>
    <ul class="albums">
        <c:forEach var="album" items="${gallery.albumEntries}">
            <c:if test="${album.photosUsed > 0}">
                <li>
                    <a id="${album.gphotoId}" onclick="return transitionTo(this.href)" href="/${album.name}${picasa.urlSuffix}">
                        <img src="${album.mediaThumbnails[0].url}">
                        <div class="title">
                            <span class="info">${album.photosUsed}</span>
                            ${album.title.plainText}
                        </div>
                    </a>
                </li>
                <c:if test="${album.geoLocation != null}">
                    <script type="text/javascript">markers.push({id: '${album.gphotoId}', pos: latLng(${album.geoLocation.latitude}, ${album.geoLocation.longitude}), title:'${album.title.plainText}'});</script>
                </c:if>
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