package net.azib.photos

class AlbumLoader(thumbSize: Int) : XMLListener<Album> {
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
        "icon" -> thumbUrl = value + ".jpg"
        "timestamp" -> timestamp = value.toLong()
        "nickname" -> author = value
        "user" -> authorId = value
        "access" -> isPublic = "public" == value
        "numphotos" -> {
          size = value.toInt()
          photos.ensureCapacity(size)
        }
        "where/Point/pos" -> geo = GeoLocation(value)
        "entry/category@term" -> when {
          value.endsWith("photo") -> {
            photo = Photo()
            photos.add(photo!!)
          }
          value.endsWith("comment") -> {
            comment = Comment()
            comments.add(comment!!)
          }
        }
      }
    }

    photo?.apply { 
      when (path) {
        "entry/id" -> id = value
        "entry/title" -> title = value
        "entry/summary" -> description = value
        "entry/timestamp" -> timestamp = value.toLong()
        "entry/width" -> width = value.toInt()
        "entry/height" -> height = value.toInt()
        "entry/content@src" -> url = replaceUrlSuffix(value)
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

  private val invalidUrlChars = "[?&!%.,:;+*/()'\"]".toRegex()
  private fun replaceUrlSuffix(url: String): String {
    var desc = photo?.description?.take(30)?.replace(invalidUrlChars, "")?.replace(' ', '-')
    if (desc.isNullOrEmpty()) desc = result.photos.size.toString()
    return url.substring(0, url.lastIndexOf('/') + 1) + "${result.name}-${desc}.jpg"
  }

  override fun end(path: String) {
    if ("entry" == path) {
      photo = null
      comment = null
    }
  }
}
