package data.db.models

import io.jsondb.annotation.Document
import io.jsondb.annotation.Id
import java.time.LocalDate

@Document(collection = "birthdays", schemaVersion = "1.0")
class BirthdayEntity @JvmOverloads constructor(@Id var id: Long = 0L, var name: String = "", var date: LocalDate = LocalDate.now())