package data.db

import data.db.models.BirthdayEntity
import data.db.models.EventEntity
import domain.models.BirthdayDuplicatedError
import domain.models.Event
import io.jsondb.JsonDBTemplate
import io.reactivex.Completable
import java.util.Date

class JsondbDataSource {

    companion object {
        private val EVENT_COLLECTION = EventEntity::class.java
        private val BIRTHDAY_COLLECTION = BirthdayEntity::class.java
        private val DATABASE_LOCATION = "database"
        private val DATABASE_PACKAGE = "data.db.models"
    }

    private var jsonDBTemplate = JsonDBTemplate(DATABASE_LOCATION, DATABASE_PACKAGE)

    fun saveEvent(event: Event): Completable {
        checkCollection(EVENT_COLLECTION)

        val entity = EventEntity(event.id, event.message)

        return Completable.fromAction {
            jsonDBTemplate.upsert<EventEntity>(entity)
        }
    }

    fun saveBirthday(name: String, date: Date): Completable {
        checkCollection(BIRTHDAY_COLLECTION)

        val entity = BirthdayEntity(System.currentTimeMillis(), name, date)

        val alreadyExists = jsonDBTemplate.getCollection(BIRTHDAY_COLLECTION)
                .firstOrNull { it.name == name && it.date == date }

        alreadyExists?.let {
            return Completable.error(BirthdayDuplicatedError())
        } ?: jsonDBTemplate.insert<BirthdayEntity>(entity)

        return Completable.complete()
    }

    private fun checkCollection(collection: Class<*>) {
        if (jsonDBTemplate.collectionExists(collection).not()) {
            jsonDBTemplate.createCollection(collection)
        }
    }
}