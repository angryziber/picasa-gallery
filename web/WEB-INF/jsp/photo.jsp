<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="photo" scope="request" type="com.google.gdata.data.photos.PhotoEntry"/>

<!DOCTYPE html>
<html>
<head>
    <title>${photo.description.plainText}</title>
    <style type="text/css">
        html, body, img {
            background:black;
            height: 100%;
            margin: 0;
            text-align: center;
            overflow-y: hidden;
        }
    </style>
</head>
<body>
    <img src="${photo.mediaContents[0].url}">
</body>
</html>
