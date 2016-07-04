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

  val thumbUrlLarge: String?
    get() = thumbUrl?.replace("/s160-c/", "/s1024/")?.replace("/s212-c/", "/s1024/")

  open fun size() = size
}
