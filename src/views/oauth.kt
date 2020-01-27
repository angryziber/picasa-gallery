package views

// language=HTML
fun oauth(refreshToken: String?) = """
<!DOCTYPE html>
<html lang="en">
<body>
  Paste this to <code>config.properties</code>:

  <pre>
  google.oauth.refreshToken=$refreshToken
  </pre>

  <a href="/">See your authorized gallery</a>
</body>
</html>  
"""
