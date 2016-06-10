package net.azib.photos

import java.util.*

class Gallery : Entity() {
  var author: String? = null
  var albums: List<Album> = ArrayList(64)
}
