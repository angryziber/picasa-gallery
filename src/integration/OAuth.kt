package integration

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth2AccessToken
import photos.Cache
import photos.Config
import java.net.HttpURLConnection

data class OAuth(var refreshToken: String?, val isDefault: Boolean = false) {
  companion object {
    val default = OAuth(Config.oauthRefreshToken, isDefault = true)

    private val service = ServiceBuilder()
      .apiKey(Config.oauthClientId)
      .apiSecret(Config.oauthClientSecret)
      .callback("http://localhost:8080/oauth")
      .build(GoogleApi20.instance())
  }

  private var expiresAt: Long = 0
  private val isExpired get() = expiresAt <= System.currentTimeMillis()

  private var token: OAuth2AccessToken? = null
    get() {
      if (isExpired) refresh()
      return field
    }
    set(value) {
      field = value?.also {
        expiresAt = System.currentTimeMillis() + it.expiresIn * 1000 - 10000
        refreshToken = it.refreshToken
      }
    }

  val profile by lazy {
    refreshToken?.let {
      Cache.get("profile:$it") {
        JsonLoader().load(this, "https://www.googleapis.com/oauth2/v1/userinfo", Profile::class, mapOf("alt" to "json"))
      }
    }
  }

  fun token(code: String) = service.getAccessToken(code).also {
    token = it
  }

  private fun refresh() {
    refreshToken.let {
      token = service.refreshAccessToken(it)
    }
  }

  fun authorize(conn: HttpURLConnection) {
    token?.apply { conn.setRequestProperty("Authorization", "$tokenType $accessToken") }
  }
}
