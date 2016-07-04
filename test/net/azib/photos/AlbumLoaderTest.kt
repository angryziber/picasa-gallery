package net.azib.photos

import org.hamcrest.CoreMatchers.equalTo
import org.jetbrains.spek.api.Spek
import org.junit.Assert.assertThat

class AlbumLoaderTest: Spek({
  it("parses album feed") {
    val album = XMLParser(AlbumLoader()).parse(Album::class.java.getResourceAsStream("album.xml"))
    assertThat(album.name, equalTo("Morocco"))
    assertThat(album.title, equalTo("Morocco"))
    assertThat(album.description, equalTo("Morocco round trip: Fes, Chefchaouen, Casablanca, Marrakech, Merzouga"))
    assertThat(album.author, equalTo("Anton Keks"))
    assertThat(album.authorId, equalTo("117440562642491680332"))
    assertThat(album.isPublic, equalTo(true))
    assertThat(album.thumbUrl, equalTo("https://lh3.googleusercontent.com/-ooqeMhFMze0/VtyhMoYRIKE/AAAAAAABNJQ/DruDrN8NmjEw-QT-28mIrn0OPW4sqwXPgCHM/s160-c/Morocco.jpg"))
    assertThat(album.timestamp, equalTo(1431327600000L))
    assertThat(album.geo!!.lat, equalTo(31.791702f))
    assertThat(album.geo!!.lon, equalTo(-7.09262f))
    assertThat(album.photos.size, equalTo(1))
    assertThat(album.comments.size, equalTo(1))

    val photo = album.photos[0]
    assertThat(photo.id, equalTo("6259054830603480962"))
    assertThat(photo.title, equalTo("20150511_090348"))
    assertThat(photo.description, equalTo("Satellite dishes of Fes Medina are the only thing that reminds us it is not 1700s anymore"))
    assertThat(photo.timestamp, equalTo(1431324228000L))
    assertThat(photo.width, equalTo(4000))
    assertThat(photo.height, equalTo(2514))
    assertThat(photo.url, equalTo("https://lh3.googleusercontent.com/-zBnTKPsUg38/VtyhNN_QL4I/AAAAAAABMHg/mK1UCQcRPycGbfAzWgHRoGGZCwUBbBcpgCHM/s1600/20150511_090348"))
    assertThat(photo.thumbUrl, equalTo("https://lh3.googleusercontent.com/-zBnTKPsUg38/VtyhNN_QL4I/AAAAAAABMHg/mK1UCQcRPycGbfAzWgHRoGGZCwUBbBcpgCHM/s144-c/20150511_090348"))
    assertThat(photo.exif.camera, equalTo("Canon EOS 5D Mark II"))
    assertThat(photo.exif.fstop, equalTo(22.0f))
    assertThat(photo.exif.exposure, equalTo(0.04f))
    assertThat(photo.exif.focal, equalTo(80.0f))
    assertThat(photo.exif.iso, equalTo("100"))
    assertThat(photo.geo!!.lat, equalTo(31.791702f))
    assertThat(photo.geo!!.lon, equalTo(-7.09262f))

    val comment = album.comments[0]
    assertThat(comment.author, equalTo("Roman Fjodorov"))
    assertThat(comment.authorId, equalTo("117865951136784338179"))
    assertThat(comment.avatarUrl, equalTo("https://lh3.googleusercontent.com/-0nl2Qc_XT0I/AAAAAAAAAAI/AAAAAAAAAAA/l91Lk4HVZgo/s48-c/117865951136784338179.jpg"))
    assertThat(comment.text, equalTo("Отлично снято :)"))
    assertThat(comment.photoId, equalTo("5464172735550834786"))
  }
})