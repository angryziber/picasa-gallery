<%@ page import="com.google.gdata.data.photos.GphotoUsername" %>
<%@ page import="com.google.gdata.data.Person" %>
<%@ page import="com.google.gdata.data.photos.GphotoThumbnail" %>
<%@ page import="net.azib.photos.Picasa" %>
<%@ page import="com.google.gdata.data.photos.AlbumFeed" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="album" scope="request" type="com.google.gdata.data.photos.AlbumFeed"/>
<jsp:useBean id="comments" scope="request" type="java.util.List"/>

<!DOCTYPE html>
<html>
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
            <c:choose>
                <c:when test="${photo != null}">
                    $('a#${photo.gphotoId}').click();
                </c:when>
                <c:otherwise>
                    if (location.hash == '#slideshow') $('a.photo').eq(0).click();
                </c:otherwise>
            </c:choose>
        });
    </script>
</head>

<body style="background:black; color: gray">

<div id="header" class="header">
    <a href="/${picasa.urlSuffix}" class="button fade">Gallery</a>
    <form id="search"><input></form>
    <h1 id="title">${album.title.plainText} <small>by ${album.nickname}</small></h1>
</div>

<div id="content">
    <h1>${album.title.plainText}</h1>
    <h2>${album.description.plainText}</h2>
    <c:if test="${album.access != 'private'}">
        <iframe class="facebook-button" scrolling="no" frameborder="0" allowtransparency="true"
                src="http://www.facebook.com/plugins/like.php?layout=button_count&action=like&width=90&height=20&colorscheme=dark&href=http://<%=request.getHeader("host")%>/${album.name}${picasa.urlSuffix}"></iframe>
    </c:if>
    <br>
    <div class="thumbs clear">
        <c:forEach var="photo" items="${album.photoEntries}">
            <c:set var="media" value="${photo.mediaContents[0]}"/>
            <a id="${photo.gphotoId}" href="${media.url}" class="photo"
               title="${photo.description.plainText}"
               rel="${media.width}x${media.height}"
               onclick="return false;"
               <c:if test="${photo.geoLocation != null}">data-coords="${photo.geoLocation.latitude}:${photo.geoLocation.longitude}"</c:if>
               <c:if test="${photo.exifTags != null}">data-exif="${photo.exifTags.apetureFNumber}:${photo.exifTags.exposureTime}:${photo.exifTags.isoEquivalent}:${photo.exifTags.focalLength}"</c:if>>
                <img src="/img/empty.png" class="missing" rel="${photo.mediaThumbnails[0].url}">
            </a>
        </c:forEach>
    </div>

</div>

<div id="photo-wrapper">
    <div class="title-wrapper"><div class="title"></div></div>
    <div id="photo-map"></div>
    <table id="photo-exif">
        <tr>
            <td id="aperture"></td>
            <td id="iso"></td>
        </tr>
        <tr>
            <td id="shutter"></td>
            <td id="focal"></td>
        </tr>
    </table>
    <div id="photo-comments">
        <c:forEach var="comment" items="${comments}">
            <div class="comment photo-${comment.photoId} hidden">
                <c:set var="author" value="${comment.authors[0]}"/>
                <img src="<%=((Person)pageContext.getAttribute("author")).getExtension(GphotoThumbnail.class).getValue()%>">
                <%String username = ((Person)pageContext.getAttribute("author")).getExtension(GphotoUsername.class).getValue();%>
                <a target="_blank" href="/<%=((AlbumFeed)request.getAttribute("album")).getUsername().equals(username) ? "" : "?by=" + username%>">
                    ${author.name}
                </a>
                <br>${comment.textContent.content.plainText}
            </div>
        </c:forEach>
    </div>
    <div id="photo-controls" class="visible">
        <div class="header clearfix">
            <a class="button first" onclick="viewer.close()">Close</a>
            <a class="button" id="slideshow" href="#slideshow">Slideshow</a>
            <a class="button" id="dec-interval">-</a>
            <span class="left"><span id="interval">3</span> sec</span>
            <a class="button" id="inc-interval">+</a>
            <span class="left" id="time-remaining"></span>
            <h1><span id="photo-controls-title">${album.title.plainText}</span> <small id="position"></small></h1>
        </div>
    </div>
</div>

</body>
</html>
