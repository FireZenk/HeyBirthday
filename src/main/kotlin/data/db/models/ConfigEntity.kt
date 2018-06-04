package data.db.models

import io.jsondb.annotation.Document
import io.jsondb.annotation.Id

@Document(collection = "config", schemaVersion = "1.0")
data class ConfigEntity @JvmOverloads constructor(@Id var id: Long = 0L, val reminderChannel: String = "general",
                                                  val reminderHour: String = "10:00")