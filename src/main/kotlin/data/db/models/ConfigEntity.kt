package data.db.models

import io.jsondb.annotation.Document
import io.jsondb.annotation.Id

@Document(collection = "config", schemaVersion = "1.0")
class ConfigEntity {

    @Id val id: Long = 0L
    lateinit var reminderChannel: String
    lateinit var reminderHour: String

    companion object {
        const val REMINDER_CHANNEL_DEFAULT = "general"
        const val REMINDER_HOUR_DEFAULT = "10:00"

        fun default(): ConfigEntity = ConfigEntity().apply {
            reminderChannel = REMINDER_CHANNEL_DEFAULT
            reminderHour = REMINDER_HOUR_DEFAULT
        }
    }
}