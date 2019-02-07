package photos

import java.util.*

open class Album(
    val thumbSize: Int? = null,
    id: String? = null,
    name: String? = null,
    title: String? = null,
    description: String? = null,
    var content: String? = null,
    var author: String? = null,
    var authorId: String? = null
) : Entity(id, title, description) {

  var size = 0
  var access = Access.private

  var name = name
    set(value) { if (value != id) field = value }

  override var title: String?
    get() = super.title
    set(value) {
      super.title = value
      if (name == null) name = value?.replace(" ", "")
    }

  val photos = ArrayList<Photo>()
  val comments = ArrayList<Comment>()

  val thumbUrlLarge: String?
    get() = thumbUrl?.replace("/s\\d+-c/".toRegex(), "/s1024/")

  open fun size() = size

  enum class Access { public, protected, private }
}
