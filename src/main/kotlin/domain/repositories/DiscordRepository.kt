package domain.repositories

import data.db.JsondbDataSource
import data.net.DiscordDataSource
import data.net.GiphyDataSource
import domain.models.Birthday
import domain.models.Event
import domain.models.ServerConnectionState
import io.reactivex.Completable
import io.reactivex.Flowable
import org.javacord.api.entity.channel.TextChannel
import java.time.LocalDate

class DiscordRepository(private val discord: DiscordDataSource,
                        private val giphy: GiphyDataSource,
                        private val database: JsondbDataSource) {

    fun listenConnection(): Flowable<ServerConnectionState> = discord.listenConnection()

    fun listenMessages(): Flowable<Event> = discord.listenMessages()

    fun sendMessage(channel: TextChannel, message: String): Completable = discord.sendMessage(channel, message)

    fun saveBirthday(serverId: Long, name: String, date: LocalDate): Completable
            = database.saveBirthday(serverId, name, date)

    fun deleteBirthday(serverId: Long, name: String): Completable = database.deleteBirthday(serverId, name)

    fun haveBirthdaysToday(serverId: Long): List<Birthday> = database.getBirthdays(serverId, LocalDate.now())

    fun sendBirthday(serverId: Long, name: String, message: String): Completable {
        return giphy.searchImage(name)?.let {
            discord.sendBirthday(database.getReminderChannel(serverId), "$message \n $it")
        } ?: discord.sendBirthday(database.getReminderChannel(serverId), message)
    }

    fun saveReminderChannel(serverId: Long, reminderChannel: String): Completable
            = database.saveReminderChannel(serverId, reminderChannel)

    fun saveReminderHour(serverId: Long, reminderHour: String): Completable
            = database.saveReminderHour(serverId, reminderHour)

    fun getReminderHour(serverId: Long): String = database.getReminderHour(serverId)
}