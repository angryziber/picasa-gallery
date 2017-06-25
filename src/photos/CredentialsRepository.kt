package photos

import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity

class CredentialsRepository {
  val datastore = DatastoreServiceFactory.getDatastoreService()

  init {
    val e = Entity("Album", "key")
    datastore.put(e)
  }
}

