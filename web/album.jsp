<%@ page import="com.google.gdata.data.photos.AlbumEntry" %>
<%@ page import="net.azib.gallery.Picasa" %>
<%@ page import="com.google.gdata.data.photos.AlbumFeed" %>
<%@ page import="com.google.gdata.data.photos.PhotoEntry" %>
<%@ page import="com.google.gdata.data.media.mediarss.MediaContent" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% AlbumFeed album = new Picasa().getAlbum(request.getPathInfo().substring(1)); %>
<html>
<head>
    <title><%=album.getTitle().getPlainText()%> by <%=album.getNickname()%></title>
    <link rel="stylesheet" media="screen" href="reset.css">
    <link rel="stylesheet" media="screen" href="shadowbox/shadowbox.css">
    <link rel="stylesheet" media="screen" href="gallery.css">
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
    <script type="text/javascript" src="shadowbox/shadowbox.js"></script>
    <script type="text/javascript">
        Shadowbox.init({
            continuous: true,
            overlayOpacity: 0.8,
            viewportPadding: 5
        });
    </script>
</head>
<body>
<div id="header">
    <a href="/" class="button">Gallery<span></span></a>
    <h1 id="title"><%=album.getTitle().getPlainText()%> <span class="by">by <%=album.getNickname()%></span></h1>
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