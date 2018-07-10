package photos

import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import util.XMLParser

class AlbumLoaderTest: StringSpec({
  val xml = Album::class.java.getResourceAsStream("album.xml")

  "parses album feed" {
    val album = XMLParser(AlbumLoader(LocalContent(null), 144)).parse(xml)
    assertThat(album.id).isEqualTo("6259054820507852961")
    assertThat(album.name).isEqualTo("Morocco")
    assertThat(album.title).isEqualTo("Morocco")
    assertThat(album.description).isEqualTo("Morocco round trip: Fes, Chefchaouen, Casablanca, Marrakech, Merzouga")
    assertThat(album.author).isEqualTo("Anton Keks")
    assertThat(album.authorId).isEqualTo("117440562642491680332")
    assertThat(album.access).isEqualTo(Album.Access.public)
    assertThat(album.isPublic).isTrue()
    assertThat(album.thumbUrl).isEqualTo("https://lh3.googleusercontent.com/-ooqeMhFMze0/VtyhMoYRIKE/AAAAAAABNJQ/DruDrN8NmjEw-QT-28mIrn0OPW4sqwXPgCHM/s160-c/Morocco.jpg")
    assertThat(album.timestamp).isEqualTo(1431327600000L)
    assertThat(album.geo!!.lat).isEqualTo(31.791702f)
    assertThat(album.geo!!.lon).isEqualTo(-7.09262f)

    assertThat(album.photos.size).isEqualTo(2)
    assertThat(album.comments.size).isEqualTo(1)

    var photo = album.photos[0]
    assertThat(photo.id).isEqualTo("6259054830603480962")
    assertThat(photo.title).isEqualTo("20150511_090348")
    assertThat(photo.description).isEqualTo("Satellite dishes of Fes Medina are the only thing that reminds us it is not 1700s anymore")
    assertThat(photo.timestamp).isEqualTo(1431324228000L)
    assertThat(photo.width).isEqualTo(4000)
    assertThat(photo.height).isEqualTo(2514)
    assertThat(photo.url).isEqualTo("https://lh3.googleusercontent.com/-zBnTKPsUg38/VtyhNN_QL4I/AAAAAAABMHg/mK1UCQcRPycGbfAzWgHRoGGZCwUBbBcpgCHM/s1920/Morocco-Satellite-dishes-of-Fes-Medina.jpg")
    assertThat(photo.thumbUrl).isEqualTo("https://lh3.googleusercontent.com/-zBnTKPsUg38/VtyhNN_QL4I/AAAAAAABMHg/mK1UCQcRPycGbfAzWgHRoGGZCwUBbBcpgCHM/s144-c/Morocco-Satellite-dishes-of-Fes-Medina.jpg")
    assertThat(photo.exif.camera).isEqualTo("Canon EOS 5D Mark II")
    assertThat(photo.exif.fstop).isEqualTo(22.0f)
    assertThat(photo.exif.exposure).isEqualTo(0.04f)
    assertThat(photo.exif.focal).isEqualTo(80.0f)
    assertThat(photo.exif.iso).isEqualTo("100")
    assertThat(photo.geo!!.lat).isEqualTo(31.791702f)
    assertThat(photo.geo!!.lon).isEqualTo(-7.09262f)

    photo = album.photos[1]
    assertThat(photo.url).isEqualTo("https://lh3.googleusercontent.com/-zBnTKPsUg38/VtyhNN_QL4I/AAAAAAABMHg/mK1UCQcRPycGbfAzWgHRoGGZCwUBbBcpgCHM/s1920/Morocco-2.jpg")

    val comment = album.comments[0]
    assertThat(comment.author).isEqualTo("Roman Fjodorov")
    assertThat(comment.authorId).isEqualTo("117865951136784338179")
    assertThat(comment.avatarUrl).isEqualTo("https://lh3.googleusercontent.com/-0nl2Qc_XT0I/AAAAAAAAAAI/AAAAAAAAAAA/l91Lk4HVZgo/s48-c/117865951136784338179.jpg")
    assertThat(comment.text).isEqualTo("Отлично снято :)")
    assertThat(comment.photoId).isEqualTo("5464172735550834786")
  }
})