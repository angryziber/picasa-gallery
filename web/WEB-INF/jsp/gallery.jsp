<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="gallery" scope="request" type="com.google.gdata.data.photos.UserFeed"/>

<html>
<head>
    <meta name="viewport" content="width=600">
    <title>${gallery.nickname} Photography</title>
    <%@include file="head.jsp"%>
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
    <script type="text/javascript">
        var markers = [];
        function latLng(lat, lon) {
            return new google.maps.LatLng(lat, lon);
        }
        $(function() {
            var bounds = new google.maps.LatLngBounds();
            var map = new google.maps.Map($('#map')[0], {
                mapTypeId: google.maps.MapTypeId.TERRAIN,
//                center: markers[0].pos,
//                zoom: 16,
                styles: [{
                    stylers: [
                      { saturation: -5 },
                      { gamma: 0.38 },
                      { lightness: -33 }
                    ]
                }]
            });
            for (var i in markers) {
                new google.maps.Marker({position: markers[i].pos, map: map, title: markers[i].title});
                bounds.extend(markers[i].pos);
            }
            map.fitBounds(bounds);
        });
    </script>
</head>
<body>
<div id="header">
    <a href="http://picasaweb.google.com/${gallery.username}" class="button"><img src="/img/picasa-logo.png">Picasaweb<span></span></a>
    <h1 id="title">${gallery.nickname} Photography</h1>
    <form onsubmit="return doSearch()"><input id="search"></form>
</div>
<div id="content">
    <ul class="albums">
        <c:forEach var="album" items="${gallery.albumEntries}">
            <li>
                <a onclick="return transitionTo(this.href)" href="/${album.name}">
                    <img src="${album.mediaThumbnails[0].url}">
                    <div class="title">
                        <span class="info">${album.photosUsed}</span>
                        ${album.title.plainText}
                    </div>
                </a>
            </li>
            <script type="text/javascript">markers.push({pos: latLng(${album.geoLocation.latitude}, ${album.geoLocation.longitude}), title:'${album.title.plainText}'});</script>
        </c:forEach>
    </ul>
    <div id="map"></div>
</div>
</body>
</html>