package net.azib.photos

import java.util.*

object Config {
  private val props = Properties().apply {
    Picasa::class.java.getResourceAsStream("/config.properties").use { load(it) }
  }

  val defaultUser = get("google.user")
  val analyticsId = get("google.analytics")
  val mapsKey = get("google.maps.key")

  operator fun get(key: String) = props[key] as String
}