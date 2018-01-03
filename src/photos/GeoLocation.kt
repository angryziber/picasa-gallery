package photos

import java.lang.Float.parseFloat

data class GeoLocation(val lat: Float, val lon: Float) {
  constructor(value: String) : this(parseFloat(value.substringBefore(" ")), parseFloat(value.substringAfter(" ")))
}
