package photos

class SearchLoader(content: LocalContent, thumbSize: Int, allowedAlbums: Collection<Album>): AlbumLoader(content, thumbSize) {
  private val allowedAlbumIds = allowedAlbums.asSequence().map { it.id }.toSet()

  override fun addPhoto(photo: Photo) =
    if (allowedAlbumIds.contains(photo.albumId))
      super.addPhoto(photo)
    else false
}