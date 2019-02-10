package photos

import java.util.*

open class Album(
    id: String? = null,
    name: String? = null,
    title: String? = null,
    description: String? = null,
    var content: String? = null
) : Entity(id, title, description) {

  var size = 0

  val contentIsLong get() = (content?.length ?: 0) > 300

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

  val thumbUrl: String?
    get() = baseUrl?.crop(212)

  open fun size() = size
}
