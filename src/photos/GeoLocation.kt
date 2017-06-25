package photos

import java.lang.Float.parseFloat

class GeoLocation(value: String) {
  val lat: Float
  val lon: Float

  init {
    val parts = value.split(" ")
    lat = parseFloat(parts[0])
    lon = parseFloat(parts[1])
  }
}
