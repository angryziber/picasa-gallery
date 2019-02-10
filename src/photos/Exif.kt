package photos

class Exif(
  val camera: String?,
  val fstop: Float?,
  val exposure: Float?,
  val focal: Float?,
  val iso: Int?
) {
  override fun toString() = "${fstop ?: ""}:${exposure ?: ""}:${iso ?: ""}:${focal ?: ""}"
}
