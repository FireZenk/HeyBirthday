package data.db.models

import io.jsondb.annotation.Document
import io.jsondb.annotation.Id

@Document(collection = "birthdays", schemaVersion = "1.0")
class BirthdayEntity @JvmOverloads constructor(@Id var id: Long = 0L, var name: String = "", var month: Int = 1,
                                               var day: Int = 1, var year: Int = 1970)