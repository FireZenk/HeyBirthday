package data.db

import data.db.models.BirthdayEntity
import data.db.models.ConfigEntity
import domain.models.Birthday
import domain.models.BirthdayDuplicatedError
import domain.models.BirthdayNotExistsError
import io.jsondb.JsonDBTemplate
import io.reactivex.Completable
import java.util.*

class JsondbDataSource {

    companion object {
        private val BIRTHDAY_COLLECTION = BirthdayEntity::class.java
        private val CONFIG_COLLECTION = ConfigEntity::class.java
        private const val DATABASE_LOCATION = "database"
        private const val DATABASE_PACKAGE = "data.db.models"
    }

    private var jsonDBTemplate = JsonDBTemplate(DATABASE_LOCATION, DATABASE_PACKAGE)

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

    fun deleteBirthday(name: String): Completable {
        checkCollection(BIRTHDAY_COLLECTION)

        val alreadyExists = jsonDBTemplate.getCollection(BIRTHDAY_COLLECTION)
                .firstOrNull { it.name == name }

        alreadyExists?.let {
            jsonDBTemplate.remove(it, BIRTHDAY_COLLECTION)
        } ?: return Completable.error(BirthdayNotExistsError())

        return Completable.complete()
    }

    fun getBirthdays(date: Date): List<Birthday> = jsonDBTemplate.getCollection(BIRTHDAY_COLLECTION)
            .filter { it.date == date }
            .map { Birthday(it.name, it.date) }

    fun saveReminderChannel(name: String): Completable = Completable.fromAction {
        jsonDBTemplate.upsert<ConfigEntity>(ConfigEntity(reminderChannel = name))
    }

    fun getReminderChannel(): String = jsonDBTemplate.getCollection(CONFIG_COLLECTION)
        .firstOrNull()?.reminderChannel ?: ConfigEntity().reminderChannel

    fun saveReminderHour(reminderHour: String): Completable = Completable.fromAction {
        jsonDBTemplate.upsert<ConfigEntity>(ConfigEntity(reminderHour = reminderHour))
    }

    fun getReminderHour(): String = jsonDBTemplate.getCollection(CONFIG_COLLECTION)
            .firstOrNull()?.reminderHour ?: ConfigEntity().reminderHour

    private fun checkCollection(collection: Class<*>) {
        if (jsonDBTemplate.collectionExists(collection).not()) {
            jsonDBTemplate.createCollection(collection)
        }
    }
}