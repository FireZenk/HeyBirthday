package domain.repositories

import data.db.JsondbDataSource
import data.net.DiscordDataSource
import domain.models.Event
import io.reactivex.Completable
import io.reactivex.Flowable
import org.javacord.api.entity.channel.TextChannel

class DiscordRepository(private val discord: DiscordDataSource, private val database: JsondbDataSource) {

    fun listenMessages(): Flowable<Event> {
        return discord.listenMessages().map {
            database.saveEvent(it).subscribe({}, {
                // On save error just print for now
                it.printStackTrace()
            })
            it
        }
    }

    fun sendMessage(channel: TextChannel, message: String): Completable {
        database.saveEvent(Event(0L, message, channel)).subscribe({}, {
            // On save error just print for now
            it.printStackTrace()
        })
        return discord.sendMessage(channel, message)
    }
}