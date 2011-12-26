<%@ page import="com.google.gdata.data.photos.AlbumEntry" %>
<%@ page import="net.azib.gallery.Picasa" %>
<%@ page import="com.google.gdata.data.photos.AlbumFeed" %>
<%@ page import="com.google.gdata.data.photos.PhotoEntry" %>
<%@ page import="com.google.gdata.data.media.mediarss.MediaContent" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String[] parts = request.getPathInfo().split("/");
AlbumFeed album = new Picasa().getAlbum(parts[1]);
String requestedPhotoId = parts.length > 2 ? parts[2] : null;
%>
<html>
<head>
    <title><%=album.getTitle().getPlainText()%> by <%=album.getNickname()%></title>
    <%@include file="head.jsp"%>
    <% if (requestedPhotoId != null) { %>
        <script type="text/javascript">
            $(window).load(function() { $('a#<%=requestedPhotoId%>').click(); });
        </script>
    <% } %>
</head>
<body>
<div id="header">
    <a href="/" class="button" onclick="return transitionTo(this.href)">Gallery<span></span></a>
    <h1 id="title"><%=album.getTitle().getPlainText()%> <span class="by">by <%=album.getNickname()%></span></h1>
    <a href="http://picasaweb.google.com/<%=album.getUsername()%>" class="button right"><img src="/img/picasa-logo.png">Picasaweb<span></span></a>
</div>
<div id="content">
    <h1><%=album.getTitle().getPlainText()%></h1>
    <h2><%=album.getDescription().getPlainText()%></h2>
    <br>

    <ul class="thumbs">
        <% for (PhotoEntry photo : album.getPhotoEntries()) { %>
            <li>
                <% MediaContent media = photo.getMediaContents().get(0); %>
                <a id="<%=photo.getGphotoId()%>" href="<%=media.getUrl()%>"
                   title="<%=photo.getDescription().getPlainText()%>"
                   rel="shadowbox[album];height=<%=media.getHeight()%>;width=<%=media.getWidth()%>">
                    <img src="<%=photo.getMediaThumbnails().get(0).getUrl()%>">
                </a>
            </li>
        <% } %>
    </ul>
</div>
</body>
</html>