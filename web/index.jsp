<%@ page import="com.google.gdata.data.photos.AlbumEntry" %>
<%@ page import="net.azib.gallery.Picasa" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Simple jsp page</title></head>
  <body>
    <ul>
    <% for (AlbumEntry album : new Picasa().getAlbums()) { %>
        <li><%=album.getTitle().getPlainText()%> (<%=album.getPhotosUsed()%>)</li>
    <% } %>
    </ul>
  </body>
</html>