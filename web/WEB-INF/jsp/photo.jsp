<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="photo" scope="request" type="com.google.gdata.data.photos.PhotoEntry"/>

<!DOCTYPE html>
<html>
<head>
    <title>${photo.description.plainText}</title>
    <meta name="viewport" content="width=650, user-scalable=no">
    <script>
        function center(img) {
            img.style.left = ((innerWidth - img.width) / 2) + 'px';
        }
    </script>
</head>

<body style="background:black; color: gray; margin: 0 auto">
    <img src="${photo.mediaContents[0].url}" style="position:absolute; height:100%;" onload="center(this)">
</body>
</html>
