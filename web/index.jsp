<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String agent = request.getHeader("User-Agent");
    if (agent != null && agent.contains("Mobile")) {
       // redirect mobile users to the real Picasaweb
       response.sendRedirect("http://picasaweb.google.com/" + Picasa.USER);
    }
    else if (request.getPathInfo().equals("/")) {
      %><%@include file="gallery.jsp"%><%
    } else {
      %><%@include file="album.jsp"%><%
    }
%>
