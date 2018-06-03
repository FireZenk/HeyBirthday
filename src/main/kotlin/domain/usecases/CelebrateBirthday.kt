package domain.usecases

import domain.repositories.DiscordRepository

class CelebrateBirthday(private val repository: DiscordRepository) {

    fun execute(message: String) = repository.sendBirthday(message)
}