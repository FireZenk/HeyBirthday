package domain.usecases

import domain.repositories.DiscordRepository

class CelebrateBirthday(private val repository: DiscordRepository) {

    fun execute(serverId: Long, name: String, message: String) = repository.sendBirthday(serverId, name, message)
}