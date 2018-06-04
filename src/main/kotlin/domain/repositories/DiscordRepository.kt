package domain.repositories

import data.db.JsondbDataSource
import data.net.DiscordDataSource
import data.net.GiphyDataSource
import domain.models.Birthday
import domain.models.Event
import io.reactivex.Completable
import io.reactivex.Flowable
import org.javacord.api.entity.channel.TextChannel
import java.time.LocalDate

class DiscordRepository(private val discord: DiscordDataSource,
                        private val giphy: GiphyDataSource,
                        private val database: JsondbDataSource) {

    fun listenMessages(): Flowable<Event> = discord.listenMessages()

    fun sendMessage(channel: TextChannel, message: String): Completable = discord.sendMessage(channel, message)

    fun saveBirthday(name: String, date: LocalDate): Completable = database.saveBirthday(name, date)

    fun deleteBirthday(name: String): Completable = database.deleteBirthday(name)

    fun haveBirthdaysToday(): List<Birthday> = database.getBirthdays(LocalDate.now())

    fun sendBirthday(name: String, message: String): Completable {
        return giphy.searchImage(name)?.let {
            discord.sendBirthday(database.getReminderChannel(), "$message \n $it")
        } ?: discord.sendBirthday(database.getReminderChannel(), message)
    }

    fun saveReminderChannel(reminderChannel: String): Completable = database.saveReminderChannel(reminderChannel)

    fun saveReminderHour(reminderHour: String): Completable = database.saveReminderHour(reminderHour)

    fun getReminderHour(): String = database.getReminderHour()
}