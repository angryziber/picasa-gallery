package net.azib.photos

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.apis.LinkedInApi20
import com.github.scribejava.core.builder.ServiceBuilder
import java.net.URL

fun main(args: Array<String>) {
  val service = ServiceBuilder()
        .apiKey("854381798570-ospd0oo53e5tajt48meuesu63glb8j0s.apps.googleusercontent.com")
        .apiSecret("wPA_x7GNkF5q3gXKyc0GgkA1")
        .callback("http://localhost:8080")
        .build(GoogleApi20.instance())

  val accessToken = service.getAccessToken("4/JnCHaDP55_nL671F8VYw2nu_q9xyOnoBSnGkAGv862k")
  println(accessToken)
}