package domain.usecases

import data.DiscordDataSource

class ListenMessages(private val dataSource: DiscordDataSource) {

    fun execute() = dataSource.listenMessages()
}