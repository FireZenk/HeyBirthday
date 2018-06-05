package domain.usecases

import domain.repositories.DiscordRepository

class DeleteBirthday(private val repository: DiscordRepository) {

    fun execute(serverId: Long, name: String) = repository.deleteBirthday(serverId, name)
}