package domain.usecases

import domain.repositories.DiscordRepository
import io.reactivex.Completable
import org.javacord.api.entity.channel.TextChannel

class SendMessage(private val repository: DiscordRepository) {

    fun execute(channel: TextChannel, message: String): Completable = repository.sendMessage(channel, message)
}