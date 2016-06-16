package net.azib.photos

class RandomPhotos(val photos: List<Photo>, val author: String?, val album: String?) {
  init {
    photos.forEach { it.url = it.url?.replace("/s1600/", "/s1920/") }
  }
}
