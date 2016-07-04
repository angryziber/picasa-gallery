package net.azib.photos

import java.util.*

open class Album : Entity() {
  var author: String? = null
  var authorId: String? = null
  var isPublic = false
  var size = 0

  var name: String? = null
    set(value) { if (value != id) field = value }

  override var title: String?
    get() = super.title
    set(value) {
      super.title = value
      if (name == null) name = value?.replace(" ", "")
    }

  var photos = ArrayList<Photo>()
  var comments = ArrayList<Comment>()

  val thumbUrlLarge: String?
    get() = thumbUrl?.replace("/s160-c/", "/s1024/")?.replace("/s212-c/", "/s1024/")

  open fun size() = size
}
