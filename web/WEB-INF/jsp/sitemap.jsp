<%@ page contentType="text/xml;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="gallery" scope="request" type="com.google.gdata.data.photos.UserFeed"/>
<urlset xmlns="http://www.google.com/schemas/sitemap/0.9">
  <url>
    <loc>http://${host}/</loc>
    <lastmod>${gallery.updated}</lastmod>
    <changefreq>weekly</changefreq>
    <priority>1</priority>
  </url>
  <c:forEach items="${gallery.albumEntries}" var="album">
    <url>
      <loc>http://${host}/${album.name}</loc>
      <lastmod>${album.updated}</lastmod>
      <changefreq>monthly</changefreq>
      <priority>0.8</priority>
    </url>
  </c:forEach>
</urlset>
