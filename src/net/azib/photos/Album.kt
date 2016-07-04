package net.azib.photos

import java.util.*

open class Album : Entity() {
  var name: String? = null
    get() = if (field == id) title?.replace(" ", "") else field
  var author: String? = null
  var authorId: String? = null
  var isPublic = false
  var size = 0

  var photos = ArrayList<Photo>()
  var comments = ArrayList<Comment>()

  open fun size() = size
}
