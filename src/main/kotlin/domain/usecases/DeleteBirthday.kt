package domain.usecases

import domain.repositories.DiscordRepository

class DeleteBirthday(private val repository: DiscordRepository) {

    fun execute(name: String) = repository.deleteBirthday(name)
}