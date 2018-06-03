package domain.usecases

import domain.repositories.DiscordRepository
import java.util.Date

class SaveBirthday(private val repository: DiscordRepository) {

    fun execute(name: String, date: Date) = repository.saveBirthday(name, date)
}