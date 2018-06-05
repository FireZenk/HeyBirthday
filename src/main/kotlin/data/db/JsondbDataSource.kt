package data.db

import data.db.models.BirthdayEntity
import data.db.models.ConfigEntity
import domain.models.Birthday
import domain.models.BirthdayDuplicatedError
import domain.models.BirthdayNotExistsError
import io.jsondb.JsonDBTemplate
import io.reactivex.Completable
import java.io.File
import java.time.LocalDate

class JsondbDataSource {

    companion object {
        private val BIRTHDAY_COLLECTION = BirthdayEntity::class.java
        private val CONFIG_COLLECTION = ConfigEntity::class.java
        private const val DATABASE_LOCATION = "database"
        private const val DATABASE_PACKAGE = "data.db.models"
    }

    fun saveBirthday(serverId: Long, name: String, date: LocalDate): Completable {
        val db = checkCollection(serverId, BIRTHDAY_COLLECTION)

        val entity = BirthdayEntity(System.currentTimeMillis(), name, date.monthValue, date.dayOfMonth, date.year)

        val alreadyExists = db.getCollection(BIRTHDAY_COLLECTION)
                .firstOrNull { it.name == name && LocalDate.of(it.year, it.month, it.day) == date }

        alreadyExists?.let {
            return Completable.error(BirthdayDuplicatedError())
        } ?: db.insert<BirthdayEntity>(entity)

        return Completable.complete()
    }

    fun deleteBirthday(serverId: Long, name: String): Completable {
        val db = checkCollection(serverId, BIRTHDAY_COLLECTION)

        val alreadyExists = db.getCollection(BIRTHDAY_COLLECTION)
                .firstOrNull { it.name == name }

        alreadyExists?.let {
            db.remove(it, BIRTHDAY_COLLECTION)
        } ?: return Completable.error(BirthdayNotExistsError())

        return Completable.complete()
    }

    fun getBirthdays(serverId: Long, date: LocalDate): List<Birthday> {
        val db = checkCollection(serverId, BIRTHDAY_COLLECTION)
        return db.getCollection(BIRTHDAY_COLLECTION)
                .filter { it.month == date.monthValue && it.day == date.dayOfMonth }
                .map { Birthday(serverId, it.name, date) }
    }

    fun saveReminderChannel(serverId: Long, name: String): Completable = Completable.fromAction {
        val db = checkCollection(serverId, CONFIG_COLLECTION)
        val config = getConfig(db)
        config.reminderChannel = name
        db.upsert<ConfigEntity>(config)
    }

    fun getReminderChannel(serverId: Long): String {
        val db = checkCollection(serverId, CONFIG_COLLECTION)
        return db.getCollection(CONFIG_COLLECTION)
                .firstOrNull()?.reminderChannel ?: getConfig(db).reminderChannel
    }

    fun saveReminderHour(serverId: Long, reminderHour: String): Completable = Completable.fromAction {
        val db = checkCollection(serverId, CONFIG_COLLECTION)
        val config = getConfig(db)
        config.reminderHour = reminderHour
        db.upsert<ConfigEntity>(config)
    }

    fun getReminderHour(serverId: Long): String {
        val db = checkCollection(serverId, CONFIG_COLLECTION)
        return db.getCollection(CONFIG_COLLECTION)
                .firstOrNull()?.reminderHour ?: getConfig(db).reminderHour
    }

    private fun getConfig(db: JsonDBTemplate): ConfigEntity = db.getCollection(CONFIG_COLLECTION).firstOrNull()
            ?: ConfigEntity.default()

    private fun checkCollection(serverId: Long, collection: Class<*>): JsonDBTemplate {
        checkDirectory(serverId)

        val db = JsonDBTemplate("$DATABASE_LOCATION/$serverId", DATABASE_PACKAGE)
        if (db.collectionExists(collection).not()) {
            db.createCollection(collection)
        }
        return db
    }

    private fun checkDirectory(serverId: Long) {
        val directory = File("$DATABASE_LOCATION/$serverId")
        if (directory.exists().not()) {
            directory.mkdir()
        }
    }
}