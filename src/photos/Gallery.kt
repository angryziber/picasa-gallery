package photos

import java.util.*

class Gallery(val thumbSize: Int) : Entity() {
  var author: String? = null
  var authorId: String? = null
  var albums: MutableMap<String, Album> = LinkedHashMap(64)

  infix operator fun plusAssign(album: Album) {
    albums[album.name!!] = album
  }
}
