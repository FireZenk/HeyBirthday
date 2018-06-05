package domain.usecases

import domain.repositories.DiscordRepository

class SaveReminderChannel(private val repository: DiscordRepository) {

    fun execute(serverId: Long, reminderChannel: String) = repository.saveReminderChannel(serverId, reminderChannel)
}