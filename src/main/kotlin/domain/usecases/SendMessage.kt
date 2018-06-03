package domain.usecases

import data.DiscordDataSource
import io.reactivex.Completable
import org.javacord.api.entity.channel.TextChannel

class SendMessage(private val discordDataSource: DiscordDataSource) {

    fun execute(channel: TextChannel, message: String): Completable = discordDataSource.sendMessage(channel, message)
}