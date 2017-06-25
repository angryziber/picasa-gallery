package util

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth2AccessToken
import photos.Config
import java.net.HttpURLConnection

object OAuth {
  private val service = ServiceBuilder()
        .apiKey(Config.oauthClientId)
        .apiSecret(Config.oauthClientSecret)
        .callback("http://localhost:8080/oauth")
        .build(GoogleApi20.instance())

  private var expiresAt: Long = 0
  private val isExpired get() = expiresAt <= System.currentTimeMillis()

  private var token: OAuth2AccessToken? = null
    get() {
      if (isExpired) refresh()
      return field
    }
    set(value) {
      field = value?.apply {
        expiresAt = System.currentTimeMillis() + expiresIn * 1000 - 10000
      }
    }

  fun token(code: String) = service.getAccessToken(code).apply {
    token = this
    Config.oauthRefreshToken = refreshToken
  }

  private fun refresh() {
    Config.oauthRefreshToken?.let {
      token = service.refreshAccessToken(it)
    }
  }

  fun authorize(conn: HttpURLConnection) {
    token?.apply { conn.setRequestProperty("Authorization", "$tokenType $accessToken") }
  }
}
