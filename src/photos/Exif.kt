package photos

class Exif(
  val camera: String?,
  val fstop: String?,
  val exposure: String?,
  val focal: String?,
  val iso: Int?
) {
  override fun toString() = "${fstop ?: ""}:${exposure ?: ""}:${iso ?: ""}:${focal ?: ""}"
}
