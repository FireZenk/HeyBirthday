package ui.commands

import domain.models.Event
import domain.usecases.SendMessage
import org.javacord.api.entity.channel.TextChannel
import org.slf4j.Logger

abstract class Command(private val sendMessage: SendMessage) {

    abstract fun getLogger(): Logger

    abstract fun processEvent(event: Event)

    protected fun sendResponse(channel: TextChannel, message: String) {
        sendMessage.execute(channel, message)
                .subscribe({}, { getLogger().debug("Discord api error", it) })
    }
}