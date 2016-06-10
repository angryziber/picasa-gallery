package net.azib.photos

import java.util.*

class AlbumLoader : XMLListener<Album> {
  private lateinit var album: Album
  private var photo: Photo? = null
  private var comment: Comment? = null

  override fun getResult() = album

  override fun rootElement(name: String) {
    album = Album()
    photo = null
    comment = null
  }

  override fun rootElementEnd(name: String) {
  }

  override fun value(path: String, value: String) {
    album.apply {
      when (path) {
        "name" -> name = value
        "title" -> title = value
        "subtitle" -> description = value
        "icon" -> thumbUrl = value
        "timestamp" -> timestamp = value.toLong()
        "nickname" -> author = value
        "user" -> authorId = value
        "access" -> isPublic = "public" == value
        "numphotos" -> {
          size = value.toInt()
          photos = ArrayList(size)
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
        "entry/content@src" -> url = value
        "entry/group/thumbnail@url" -> thumbUrl = value
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

  override fun start(path: String) {
  }

  override fun end(path: String) {
    if ("entry" == path) {
      photo = null
      comment = null
    }
  }
}
