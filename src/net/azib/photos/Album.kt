package net.azib.photos

import java.util.*

open class Album : Entity() {
  var name: String? = null
  var author: String? = null
  var authorId: String? = null
  var isPublic = false
  var size = 0

  var photos: MutableList<Photo> = ArrayList()
  var comments: MutableList<Comment> = ArrayList()

  open fun size() = size
}
