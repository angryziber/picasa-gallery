<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="random" scope="request" type="com.google.gdata.data.photos.GphotoEntry"/>

<!DOCTYPE html>
<html>
<head>
    <title>Random photo by ${picasa.user}</title>
    <style type="text/css">
        html, body, img {
            background: black;
            height: 100%;
            margin: 0;
            text-align: center;
            overflow-y: hidden;
        }
        #title {
            position: absolute;
            bottom: 10px;
            right: 20px;
            color: white;
            padding: 3px 5px;
            background: rgba(0,0,0,0.3);
            border-radius: 4px;
            font-family: sans-serif;
        }
    </style>
    <script type="text/javascript">
        var img = new Image();
        img.onload = function() {
            document.body.appendChild(img);
        };
        img.src = '${random.content.uri}';
    </script>
</head>
<body>
    <div id="title">${picasa.nickname}</div>
</body>
</html>
