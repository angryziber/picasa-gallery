<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="album" scope="request" type="com.google.gdata.data.photos.AlbumFeed"/>

<!DOCTYPE html>
<html xmlns:fb="http://ogp.me/ns/fb#">
<head>
    <title>${album.title.plainText} by ${album.nickname} - Photos</title>
    <meta name="viewport" content="width=650, user-scalable=no">
    <meta name="description" content="${album.description.plainText}">
    <meta name="keywords" content="${album.nickname},photos,${fn:replace(album.title.plainText, " ", ",")},${fn:replace(album.description.plainText, " ", ",")}">

    <c:if test="${photo == null}">
        <meta name="medium" content="image">
        <meta property="og:type" content="photos:album">
        <meta property="og:title" content="${album.title.plainText} by ${album.nickname}">
        <meta property="og:image" content="${album.icon}">
        <link rel="image_src" href="${album.icon}">
        <c:if test="${album.geoLocation != null}">
            <meta property="og:latitude" content="${album.geoLocation.latitude}">
            <meta property="og:longitude" content="${album.geoLocation.longitude}">
        </c:if>
    </c:if>
    <c:if test="${photo != null}">
        <meta name="medium" content="image">
        <meta property="og:type" content="photos:photo">
        <c:if test="${fn:length(photo.description.plainText) > 0}"><meta property="og:title" content="${photo.description.plainText}"></c:if>
        <c:if test="${fn:length(photo.description.plainText) == 0}"><meta property="og:title" content="${album.title.plainText}"></c:if>
        <meta property="og:image" content="${photo.mediaThumbnails[0].url}">
        <c:if test="${photo.geoLocation != null}">
            <meta property="og:latitude" content="${photo.geoLocation.latitude}">
            <meta property="og:longitude" content="${photo.geoLocation.longitude}">
        </c:if>
    </c:if>
    <meta property="og:description" content="${album.description.plainText}">
    <meta property="og:site_name" content="${album.nickname} Photography">

    <%@include file="head.jsp"%>
    <script type="text/javascript">
        var viewer = new PhotoViewer();
        $(window).load(function() {
            if ($(window).width() > 1550) {
                $('a.photo').each(function(i, link) {
                    link.href = link.href.replace('/s1024/', '/s1600/');
                });
            }
            viewer.setup();
            <c:if test="${photo != null}">
            $('a#${photo.gphotoId}').click();
            </c:if>
        });
    </script>
</head>

<body style="background:black; color: gray">

<div id="fb-root"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_GB/all.js#xfbml=1";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>

<div id="header" class="header">
    <a href="/${picasa.urlSuffix}" class="button fade">Gallery<span></span></a>
    <form id="search"><input></form>
    <h1 id="title">${album.title.plainText} <small>by ${album.nickname}</small></h1>
</div>

<div id="content">
    <h1>${album.title.plainText}</h1>
    <h2>${album.description.plainText}</h2>
    <div class="facebook-button">
        <fb:like href="href=http://<%=request.getHeader("host")%>/${album.name}${picasa.urlSuffix}" send="false" layout="button_count" width="90" show_faces="false" ></fb:like>
    </div>
    <br>
    <div class="thumbs clear">
        <c:forEach var="photo" items="${album.photoEntries}">
            <c:set var="media" value="${photo.mediaContents[0]}"/>
            <a id="${photo.gphotoId}" href="${media.url}" class="photo"
               title="${photo.description.plainText}"
               rel="${media.width}x${media.height}"
               onclick="return false;"
               <c:if test="${photo.geoLocation != null}">coords="${photo.geoLocation.latitude}:${photo.geoLocation.longitude}"</c:if>>
                <img src="/img/empty.png" class="missing" rel="${photo.mediaThumbnails[0].url}">
            </a>
        </c:forEach>
    </div>

</div>

<div id="photo-wrapper">
    <div class="title-wrapper"><div class="title"></div></div>
    <div id="photo-map"></div>
    <div id="photo-controls" class="visible">
        <div class="header">
            <a class="button first" onclick="viewer.close()">Close<span></span></a>
            <a class="button" id="slideshow">Slideshow<span></span></a>
            <a class="button" id="decInterval">-<span></span></a>
            <span class="left"><span id="interval">3</span> sec</span>
            <a class="button" id="incInterval">+<span></span></a>
            <span class="left" id="timeRemaining"></span>
            <h1>${album.title.plainText} <small id="position"></small></h1>
        </div>
    </div>
    <div id="internal-photo-wrapper">
    </div>
</div>

</body>
</html>
