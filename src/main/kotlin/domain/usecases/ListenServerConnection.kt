package domain.usecases

import domain.repositories.DiscordRepository

class ListenServerConnection(private val repository: DiscordRepository) {

    fun execute() = repository.listenConnection()
}