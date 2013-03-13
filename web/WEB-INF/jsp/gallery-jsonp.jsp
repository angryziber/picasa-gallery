<%@ page contentType="text/xml;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="gallery" scope="request" type="com.google.gdata.data.photos.UserFeed"/>
${callback}([
    <c:forEach items="${gallery.albumEntries}" var="album">{
        id: ${album.gphotoId},
        img: '${album.mediaThumbnails[0].url}',
        link: '/${album.name}${picasa.urlSuffix}',
        title: '${album.title.plainText}'
    },</c:forEach>
]);
