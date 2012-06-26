<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="picasa" scope="request" type="net.azib.photos.Picasa"/>

<meta property="og:url" content="http://${host}${pageContext.request.servletPath}">
<meta property="fb:admins" content="${picasa.user}"/>

<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">

<link rel="stylesheet" href="/reset.css">
<link rel="stylesheet" href="/gallery.css?v=4">
<link rel="apple-touch-icon" href="/img/picasa-logo.png"/>
<link rel="stylesheet" href="/leaflet.css" />

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="/gallery.js?v=4"></script>
<script type="text/javascript" src="/leaflet.js"></script>

<c:if test="${picasa.analytics != null}">
<script type="text/javascript">
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', '${picasa.analytics}']);
    _gaq.push(['_setDomainName', location.hostname.substring(location.hostname.lastIndexOf('.', 3)+1)]);
    _gaq.push(['_trackPageview']);
    (function() {
        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
    })();
</script>
</c:if>