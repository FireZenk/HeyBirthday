package data.db.models

import io.jsondb.annotation.Document
import io.jsondb.annotation.Id

@Document(collection = "events", schemaVersion = "1.0")
data class EventEntity @JvmOverloads constructor(@Id var id: Long = 0L, var message: String = "",
                                                 val timestamp: Long = System.currentTimeMillis())
