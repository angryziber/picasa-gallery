package net.azib.photos

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.builder.ServiceBuilder

object OAuth {
  val service = ServiceBuilder()
        .apiKey(Config.oauthClientId)
        .apiSecret(Config.oauthClientSecret)
        .callback("http://localhost:8080/oauth")
        .build(GoogleApi20.instance())

  fun token(code: String) = service.getAccessToken(code)
}

fun main(args: Array<String>) {
//  val accessToken = service.getAccessToken("4/azWaL9_ihHxviLbgGELVjNDVAVoQZfV2JrI3Vus6h9A")
//  println(accessToken)
  println(OAuth.service.refreshAccessToken("1/tnlxtSq8fVkaYhSorNDC8ZTfygV6esOcAwApwi--6fc"))
}