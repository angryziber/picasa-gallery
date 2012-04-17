<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="photo" scope="request" type="com.google.gdata.data.photos.GphotoEntry"/>

<!DOCTYPE html>
<html>
<head>
    <title>${photo.description.plainText}</title>
    <style type="text/css">
        html, body, img {
            background: black;
            height: 100%;
            margin: 0;
            text-align: center;
            overflow-y: hidden;
        }
    </style>
    <script type="text/javascript">
        var img = new Image();
        img.onload = function() {
            document.body.appendChild(img);
        };
        img.src = '${photo.content.uri}';
    </script>
</head>
<body>

</body>
</html>
