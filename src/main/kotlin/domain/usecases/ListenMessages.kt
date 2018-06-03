package domain.usecases

import domain.repositories.DiscordRepository

class ListenMessages(private val repository: DiscordRepository) {

    fun execute() = repository.listenMessages()
}