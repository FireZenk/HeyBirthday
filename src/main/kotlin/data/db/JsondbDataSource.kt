package data.db

import data.db.models.EventEntity
import domain.models.Event
import io.jsondb.JsonDBTemplate
import io.reactivex.Completable

class JsondbDataSource {

    companion object {
        private val COLLECTION = EventEntity::class.java
        private val DATABASE_LOCATION = "database"
        private val DATABASE_PACKAGE = "data.db.models"
    }

    private var jsonDBTemplate = JsonDBTemplate(DATABASE_LOCATION, DATABASE_PACKAGE)

    fun saveEvent(event: Event): Completable {
        checkCollection()

        val entity = EventEntity(event.id, event.message)

        return Completable.fromAction {
            jsonDBTemplate.upsert<EventEntity>(entity)
        }
    }

    private fun checkCollection() {
        if (jsonDBTemplate.collectionExists(COLLECTION).not()) {
            jsonDBTemplate.createCollection(COLLECTION)
        }
    }
}