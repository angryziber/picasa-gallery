<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% if (request.getPathInfo().equals("/")) { %>
    <%@include file="gallery.jsp"%>
<% } else { %>
    <%@include file="album.jsp"%>
<% } %>
