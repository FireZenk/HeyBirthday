package ui

import data.db.JsondbDataSource
import data.net.DiscordDataSource
import data.net.GiphyDataSource
import domain.repositories.DiscordRepository
import domain.usecases.*
import ui.commands.*

class DependencyTree(private val discordToken: String, private val giphyToken: String) {

    private val repository: DiscordRepository by lazy {
        DiscordRepository(DiscordDataSource(discordToken), GiphyDataSource(giphyToken), JsondbDataSource())
    }

    val listenMessages: ListenMessages by lazy { ListenMessages(repository) }
    val listenBirthdays: ListenBirthdays by lazy { ListenBirthdays(repository) }
    val celebrateBirthday by lazy { CelebrateBirthday(repository) }

    private val sendMessage: SendMessage by lazy { SendMessage(repository) }
    private val saveBirthday by lazy { SaveBirthday(repository) }
    private val deleteBirthday by lazy { DeleteBirthday(repository) }
    private val saveReminderChannel by lazy { SaveReminderChannel(repository) }
    private val saveReminderHour by lazy { SaveReminderHour(repository) }

    // Commands
    val addBirthday: AddBirthday by lazy { AddBirthday(sendMessage, saveBirthday) }
    val removeBirthday: RemoveBirthday by lazy { RemoveBirthday(sendMessage, deleteBirthday) }
    val reminderChannel: ReminderChannel by lazy { ReminderChannel(sendMessage, saveReminderChannel) }
    val reminderHour: ReminderHour by lazy { ReminderHour(sendMessage, saveReminderHour) }
    val info: Info by lazy { Info(sendMessage) }
}