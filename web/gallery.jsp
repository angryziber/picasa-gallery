<%@ page import="com.google.gdata.data.photos.AlbumEntry" %>
<%@ page import="net.azib.gallery.Picasa" %>
<%@ page import="com.google.gdata.data.photos.UserFeed" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% UserFeed gallery = new Picasa().getGallery(); %>
<html>
<head>
    <title>Photos by <%=gallery.getNickname()%></title>
    <%@include file="head.jsp"%>
</head>
<body>
<div id="header">
    <h1 id="title"><%=gallery.getNickname()%> Photography</h1>
    <a href="http://picasaweb.google.com/<%=gallery.getUsername()%>" class="button right"><img src="/img/picasa-logo.png">Picasaweb<span></span></a>
</div>
<div id="content">
    <ul class="albums">
        <% for (AlbumEntry album : gallery.getAlbumEntries()) { %>
            <li>
                <a onclick="return transitionTo(this.href)" href="/<%=album.getName()%>">
                    <img src="<%=album.getMediaThumbnails().get(0).getUrl()%>">
                    <div class="title">
                        <span class="info"><%=album.getPhotosUsed()%></span>
                        <%=album.getTitle().getPlainText()%>
                    </div>
                </a>
            </li>
        <% } %>
    </ul>
</div>
</body>
</html>