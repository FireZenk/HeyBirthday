package domain.usecases

import domain.repositories.DiscordRepository
import java.time.LocalDate

class SaveBirthday(private val repository: DiscordRepository) {

    fun execute(name: String, date: LocalDate) = repository.saveBirthday(name, date)
}