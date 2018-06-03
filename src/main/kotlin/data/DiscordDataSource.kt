package data

import domain.models.Event
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.channel.TextChannel

class DiscordDataSource(token: String) {

    private val api = DiscordApiBuilder().setToken(token).login().join()

    private val publisher: PublishProcessor<Event> = PublishProcessor.create()
    private val flowable = publisher.onBackpressureLatest()

    fun listenMessages(): Flowable<Event> {
        println("You can invite the bot by using the following url: " + api.createBotInvite())

        api.addMessageCreateListener({ event ->
            publisher.onNext(Event(event.messageId, event.message.content, event.channel))
        })

        return flowable
    }

    fun sendMessage(channel: TextChannel, message: String): Completable
            = Completable.fromAction { channel.sendMessage(message) }
}