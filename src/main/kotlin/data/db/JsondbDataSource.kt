package data.db

import data.db.models.BirthdayEntity
import data.db.models.ConfigEntity
import domain.models.Birthday
import domain.models.BirthdayDuplicatedError
import domain.models.BirthdayNotExistsError
import io.jsondb.JsonDBTemplate
import io.reactivex.Completable
import java.time.LocalDate

class JsondbDataSource {

    companion object {
        private val BIRTHDAY_COLLECTION = BirthdayEntity::class.java
        private val CONFIG_COLLECTION = ConfigEntity::class.java
        private const val DATABASE_LOCATION = "database"
        private const val DATABASE_PACKAGE = "data.db.models"
    }

    private var jsonDBTemplate = JsonDBTemplate(DATABASE_LOCATION, DATABASE_PACKAGE)

    fun saveBirthday(name: String, date: LocalDate): Completable {
        checkCollection(BIRTHDAY_COLLECTION)

        val entity = BirthdayEntity(System.currentTimeMillis(), name, date.monthValue, date.dayOfMonth, date.year)

        val alreadyExists = jsonDBTemplate.getCollection(BIRTHDAY_COLLECTION)
                .firstOrNull { it.name == name && LocalDate.of(it.year, it.month, it.day) == date }

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

    fun getBirthdays(date: LocalDate): List<Birthday> = jsonDBTemplate.getCollection(BIRTHDAY_COLLECTION)
            .filter { it.month == date.monthValue && it.day == date.dayOfMonth }
            .map { Birthday(it.name, date) }

    fun saveReminderChannel(name: String): Completable = Completable.fromAction {
        val config = getConfig()
        config.reminderChannel = name
        jsonDBTemplate.upsert<ConfigEntity>(config)
    }

    fun getReminderChannel(): String = jsonDBTemplate.getCollection(CONFIG_COLLECTION)
        .firstOrNull()?.reminderChannel ?: getConfig().reminderChannel

    fun saveReminderHour(reminderHour: String): Completable = Completable.fromAction {
        val config = getConfig()
        config.reminderHour = reminderHour
        jsonDBTemplate.upsert<ConfigEntity>(config)
    }

    fun getReminderHour(): String = jsonDBTemplate.getCollection(CONFIG_COLLECTION)
            .firstOrNull()?.reminderHour ?: getConfig().reminderHour

    private fun getConfig() = jsonDBTemplate.getCollection(CONFIG_COLLECTION).firstOrNull()
            ?: ConfigEntity.default()

    private fun checkCollection(collection: Class<*>) {
        if (jsonDBTemplate.collectionExists(collection).not()) {
            jsonDBTemplate.createCollection(collection)
        }
    }
}