<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="picasa" scope="request" type="net.azib.photos.Picasa"/>

<meta property="og:url" content="http://${host}${pageContext.request.servletPath}">
<meta property="fb:admins" content="${picasa.user}"/>

<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">

<link rel="stylesheet" type="text/css" href="/reset.css">
<link rel="stylesheet" type="text/css" href="/gallery.css">
<link rel="apple-touch-icon" href="/img/picasa-logo.png"/>

<script type="text/javascript" src="/prefixfree.min.js"></script>
<script type="text/javascript" src="//code.jquery.com/jquery-1.12.1.min.js"></script>
<script type="text/javascript" src="/gallery.js?123"></script>
<script type="text/javascript" src="/chromecast.js"></script>

<script type="text/javascript">
  <c:if test="${picasa.analytics != null}">
    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

    ga('create', '${picasa.analytics}', 'auto');
    ga('send', 'pageview');
  </c:if>
</script>
