package net.azib.photos

import java.util.*

class Gallery : Entity() {
  var author: String? = null
  var albums: MutableList<Album> = ArrayList(64)
}
