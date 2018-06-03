package domain.usecases

import domain.repositories.DiscordRepository

class CelebrateBirthday(private val repository: DiscordRepository) {

    fun execute(name: String, message: String) = repository.sendBirthday(name, message)
}