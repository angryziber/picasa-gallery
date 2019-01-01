package photos

import util.XMLListener

open class AlbumLoader(val content: LocalContent, thumbSize: Int) : XMLListener<Album> {
  private var photo: Photo? = null
  private var comment: Comment? = null
  override val result = Album(thumbSize)

  override fun value(path: String, value: String) {
    result.apply {
      when (path) {
        "id" -> id = value
        "name" -> name = value
        "title" -> title = value
        "subtitle" -> description = value
        "icon" -> thumbUrl = "$value.jpg"
        "timestamp" -> timestamp = value.toLong()
        "nickname" -> author = value
        "user" -> authorId = value
        "access" -> access = Album.Access.valueOf(value)
        "numphotos" -> {
          size = value.toInt()
          photos.ensureCapacity(size)
        }
        "where/Point/pos" -> geo = GeoLocation(value)
        "entry/category@term" -> when {
          value.endsWith("photo") -> photo = Photo()
          value.endsWith("comment") -> comment = Comment()
        }
      }
    }

    photo?.apply { 
      when (path) {
        "entry/id" -> id = value
        "entry/albumid" -> albumId = value
        "entry/title" -> title = value
        "entry/summary" -> description = value
        "entry/timestamp" -> timestamp = value.toLong()
        "entry/width" -> width = value.toInt()
        "entry/height" -> height = value.toInt()
        "entry/content@src" -> url = replaceUrlSuffix(value).replace("/s1600/", "/s1920/")
        "entry/group/thumbnail@url" -> thumbUrl = replaceUrlSuffix(value)
        "entry/tags/fstop" -> exif.fstop = value.toFloat()
        "entry/tags/exposure" -> exif.exposure = value.toFloat()
        "entry/tags/focallength" -> exif.focal = value.toFloat()
        "entry/tags/iso" -> exif.iso = value
        "entry/tags/model" -> exif.camera = value
        "entry/where/Point/pos" -> geo = GeoLocation(value)
      }
    }
    
    comment?.apply {
      when (path) {
        "entry/content" -> text = value
        "entry/author/name" -> author = value
        "entry/author/thumbnail" -> avatarUrl = value
        "entry/author/user" -> authorId = value
        "entry/photoid" -> photoId = value
      }
    }
  }

  private val invalidUrlChars = "[^a-zA-Z0-9\\s-]".toRegex()
  private fun replaceUrlSuffix(url: String): String {
    var desc = photo?.description?.take(30)?.replace(invalidUrlChars, "")?.replace("\\s+".toRegex(), "-")
    if (desc.isNullOrEmpty()) desc = result.photos.size.toString()
    return url.substring(0, url.lastIndexOf('/') + 1) + "${result.name}-${desc}.jpg"
  }

  open protected fun addPhoto(photo: Photo) = result.photos.add(photo)
  open protected fun addComment(comment: Comment) = result.comments.add(comment)

  override fun end(path: String) {
    if ("entry" == path) {
      photo?.let(::addPhoto)
      photo = null
      comment?.let(::addComment)
      comment = null
      content.applyTo(result)
    }
  }
}
