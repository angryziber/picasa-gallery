<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="album" scope="request" type="com.google.gdata.data.photos.AlbumFeed"/>

<html>
<head>
    <meta name="viewport" content="width=710">
    <title>${album.title.plainText} by ${album.nickname}</title>
    <%@include file="head.jsp"%>
    <c:if test="${photoId != null}">
        <script type="text/javascript">
            $(window).load(function() { $('a#${photoId}').click(); });
        </script>
    </c:if>
</head>
<body>
<div id="header">
    <a href="/" class="button" onclick="return transitionTo(this.href)">Gallery<span></span></a>
    <h1 id="title">${album.title.plainText} <span class="by">by ${album.nickname}</span></h1>
    <form onsubmit="return doSearch()"><input id="search"></form>
</div>
<div id="content">
    <h1>${album.title.plainText}</h1>
    <h2>${album.description.plainText}</h2>
    <br>

    <ul class="thumbs">
        <c:forEach var="photo" items="${album.photoEntries}">
            <li>
                <c:set var="media" value="${photo.mediaContents[0]}"/>
                <a id="${photo.gphotoId}" href="${media.url}"
                   title="${photo.description.plainText}"
                   rel="shadowbox[album];height=${media.height};width=${media.width}">
                    <img src="${photo.mediaThumbnails[0].url}">
                </a>
            </li>
        </c:forEach>
    </ul>
</div>
</body>
</html>