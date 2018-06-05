package data.net

import domain.models.ConnectionSate
import domain.models.Event
import domain.models.ServerConnectionState
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.channel.TextChannel

class DiscordDataSource(token: String) {

    private val api = DiscordApiBuilder().setToken(token).login().join()

    private val connectionStatePublisher: PublishProcessor<ServerConnectionState> = PublishProcessor.create()
    private val connectionStateFlowable = connectionStatePublisher.onBackpressureLatest()

    private val eventPublisher: PublishProcessor<Event> = PublishProcessor.create()
    private val eventFlowable = eventPublisher.onBackpressureLatest()

    fun listenConnection(): Flowable<ServerConnectionState> {
        api.addServerBecomesAvailableListener {
            connectionStatePublisher.onNext(ServerConnectionState(it.server.id, ConnectionSate.Available()))
        }
        api.addServerBecomesUnavailableListener {
            connectionStatePublisher.onNext(ServerConnectionState(it.server.id, ConnectionSate.Unavailable()))
        }
        return connectionStateFlowable
    }

    fun listenMessages(): Flowable<Event> {
        println("You can invite the bot by using the following url: " + api.createBotInvite())

        api.addMessageCreateListener({ event ->
            eventPublisher.onNext(Event(event.server.get().id, event.messageId, event.message.content, event.channel))
        })

        return eventFlowable
    }

    fun sendMessage(channel: TextChannel, message: String): Completable
            = Completable.fromAction { channel.sendMessage(message) }

    fun sendBirthday(reminderChannel: String, message: String): Completable {
        val availableChannels: List<ServerTextChannel> = api.channels
                .filter { it is ServerTextChannel }
                .map { it as ServerTextChannel }

        val channel = availableChannels.firstOrNull { it.name == reminderChannel }

        return channel?.let {
            Completable.fromAction { channel.sendMessage(message) }
        } ?: Completable.error(Exception())
    }
}