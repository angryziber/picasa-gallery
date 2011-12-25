<%@ page import="com.google.gdata.data.photos.AlbumEntry" %>
<%@ page import="net.azib.gallery.Picasa" %>
<%@ page import="com.google.gdata.data.photos.AlbumFeed" %>
<%@ page import="com.google.gdata.data.photos.PhotoEntry" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% AlbumFeed album = new Picasa().getAlbum(request.getPathInfo().substring(1)); %>
<html>
<head>
    <title><%=album.getTitle().getPlainText()%></title>
    <link rel="stylesheet" media="screen" href="reset.css">
    <link rel="stylesheet" media="screen" href="gallery.css">
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
    <%--<script type="text/javascript" src="gallery.js"></script>--%>
</head>
<body>
<div id="header">
    <h1 id="title"><%=album.getTitle().getPlainText()%></h1>
</div>
<div id="content">
    <h1><%=album.getTitle().getPlainText()%></h1>
    <h2><%=album.getDescription().getPlainText()%></h2>
    <br>

    <ul class="thumbs">
        <% for (PhotoEntry photo : album.getPhotoEntries()) { %>
            <li>
                <a href="<%=photo.getMediaContents().get(0).getUrl()%>">
                    <img src="<%=photo.getMediaThumbnails().get(0).getUrl()%>">
                </a>
            </li>
        <% } %>
    </ul>
</div>
</body>
</html>