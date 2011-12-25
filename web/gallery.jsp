<%@ page import="com.google.gdata.data.photos.AlbumEntry" %>
<%@ page import="net.azib.gallery.Picasa" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% Picasa picasa = new Picasa(); %>
<html>
<head>
    <title><%=picasa.getTitle()%></title>
    <link rel="stylesheet" media="screen" href="reset.css">
    <link rel="stylesheet" media="screen" href="gallery.css">
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
    <%--<script type="text/javascript" src="gallery.js"></script>--%>
</head>
<body>
<div id="header">
    <h1 id="title"><%=picasa.getTitle()%></h1>
</div>
<div id="content">
    <ul class="albums">
        <% for (AlbumEntry album : picasa.getAlbums()) { %>
            <li>
                <a href="<%=album.getName()%>">
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