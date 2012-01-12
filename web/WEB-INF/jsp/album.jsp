<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="album" scope="request" type="com.google.gdata.data.photos.AlbumFeed"/>

<!DOCTYPE html>
<html>
<head>
    <title>${album.title.plainText} by ${album.nickname}</title>
    <meta name="description" content="${album.description.plainText}">
    <meta name="keywords" content="${album.nickname},photos,${fn:replace(album.title.plainText, " ", ",")},${fn:replace(album.description.plainText, " ", ",")}">
    <%@include file="head.jsp"%>
    <script type="text/javascript">
        $(window).load(function() {
            new PhotoViewer().setup();
            <c:if test="${photoId != null}">
            $('a#${photoId}').click();
            </c:if>
        });
    </script>
</head>
<body style="background:black">
<div id="header">
    <a href="/${picasa.urlSuffix}" class="button fade">Gallery<span></span></a>
    <h1 id="title">${album.title.plainText} <span class="by">by ${album.nickname}</span></h1>
    <form id="search"><input></form>
</div>
<div id="content">
    <h1>${album.title.plainText}</h1>
    <h2>${album.description.plainText}</h2>
    <iframe id="facebook-album-button" scrolling="no" frameborder="0" allowtransparency="true"
            src="http://www.facebook.com/plugins/like.php?href=http://<%=request.getHeader("host")%>/${album.name}${picasa.urlSuffix}&layout=button_count&action=like&width=90&height=20&colorscheme=dark"></iframe>
    <br>
    <ul class="thumbs clear">
        <c:forEach var="photo" items="${album.photoEntries}">
            <li>
                <c:set var="media" value="${photo.mediaContents[0]}"/>
                <a id="${photo.gphotoId}" href="${media.url}" class="photo"
                   title="${photo.description.plainText}"
                   rel="${media.width}x${media.height}"
                   <c:if test="${photo.geoLocation != null}">coords="${photo.geoLocation.latitude}:${photo.geoLocation.longitude}"</c:if>>
                    <img src="/img/empty.png" class="missing" rel="${photo.mediaThumbnails[0].url}">
                </a>
            </li>
        </c:forEach>
    </ul>

</div>
</body>
</html>