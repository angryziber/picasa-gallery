package photos

import integration.AlbumsResponse
import integration.Http
import integration.JsonLoader
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat

class JsonLoaderTest: StringSpec({
  "load all pages" {
    val http = mockk<Http>()
    every { http.send(any(), "http://albums?pageSize=50") } returns
        JsonLoader::class.java.getResourceAsStream("albums.json")
    every { http.send(any(), "http://albums?pageSize=50&pageToken=CkQKPnR5cGUuZ29vZ2xlYXBpcy5jb20vZ29vZ2xlLnBob3Rvcy5saWJyYXJ5LnYxLkxpc3RBbGJ1bXNSZXF1ZXN0EgIIMhL3AkFIX3VRNDM4cG5VLW14XzFmVTUtMVU1UXk2LXhJcmg0ek5YMTFvbHAtZ0xjOFMxSGdXWDZTcEVsNmZ1OWlFRzhUOWNEc09Va05mUjlteThfVUozejdDLVpMUXgtc2QyUDdQTnVSLXpyXzlKNTJBRUZldk5FRUR0ZS02OUVlOFJKQ2hMY05kV2piZGVXMGFaVnB1emFyT3hkVmdWbXZtaVlIVDRmVTFEa0JPNGgwVlhVWVF2enBzc3REaTU2UFpxVDZTN210cnZvb29SUy1LSjFlc0ZiRU92Tk96LVg0MnBKbFdXRHZPa1ZJRjVpRDdiSzhhVWtWaUdBQXpTUC1aSVNJSzE0ZFJLM2V2VEZmQmtoN1U5WHYwS0N6WjJLREllMG9mdWtpbHlkWlRhV2FNbTJfVm5zYThHc2pBYUZETTRMX1V4NnpDXzRqalVuTUN4eXpxX1BRNkVZWF93U3psM2NqaGtpVnZoczJNdUpfZWhYVEJXUkpwZw") } returns
        JsonLoader::class.java.getResourceAsStream("albums2.json")
    val loader = JsonLoader(http)
    val items = loader.loadAll(mockk(), "http://albums", AlbumsResponse::class)
    assertThat(items.size).isEqualTo(8)
  }
})
