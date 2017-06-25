package net.azib.photos

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.builder.ServiceBuilder
import java.net.HttpURLConnection

object OAuth {
  private val service = ServiceBuilder()
        .apiKey(Config.oauthClientId)
        .apiSecret(Config.oauthClientSecret)
        .callback("http://localhost:8080/oauth")
        .build(GoogleApi20.instance())

  private var token = Config.oauthRefreshToken?.let { service.refreshAccessToken(it) }

  fun token(code: String) = service.getAccessToken(code).apply {
    token = this
    Config.oauthRefreshToken = refreshToken
  }

  fun authorize(conn: HttpURLConnection) {
    token?.apply { conn.setRequestProperty("Authorization", "$tokenType $accessToken") }
  }
}
