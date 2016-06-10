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
    when (path) {
      "name" -> album.name = value
      "title" -> album.title = value
      "subtitle" -> album.description = value
      "icon" -> album.thumbUrl = value
      "timestamp" -> album.timestamp = value.toLong()
      "nickname" -> album.author = value
      "user" -> album.authorId = value
      "access" -> album.isPublic = "public" == value
      "numphotos" -> {
        album.size = value.toInt()
        album.photos = ArrayList(album.size)
      }
      "where/Point/pos" -> album.geo = GeoLocation(value)
      "entry/category@term" ->
        if (value.endsWith("photo")) {
          photo = Photo()
          album.photos.add(photo!!)
        }
        else if (value.endsWith("comment")) {
          comment = Comment()
          album.comments.add(comment!!)
        }
    }

    photo?.let { photo ->
      when (path) {
        "entry/id" -> photo.id = value
        "entry/title" -> photo.title = value
        "entry/summary" -> photo.description = value
        "entry/timestamp" -> photo.timestamp = value.toLong()
        "entry/width" -> photo.width = value.toInt()
        "entry/height" -> photo.height = value.toInt()
        "entry/content@src" -> photo.url = value
        "entry/group/thumbnail@url" -> photo.thumbUrl = value
        "entry/tags/fstop" -> photo.exif.fstop = value.toFloat()
        "entry/tags/exposure" -> photo.exif.exposure = value.toFloat()
        "entry/tags/focallength" -> photo.exif.focal = value.toFloat()
        "entry/tags/iso" -> photo.exif.iso = value
        "entry/tags/model" -> photo.exif.camera = value
        "entry/where/Point/pos" -> photo.geo = GeoLocation(value)
      }
    }
    
    comment?.let { comment ->
      when (path) {
        "entry/content" -> comment.text = value
        "entry/author/name" -> comment.author = value
        "entry/author/thumbnail" -> comment.avatarUrl = value
        "entry/author/user" -> comment.authorId = value
        "entry/photoid" -> comment.photoId = value
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
