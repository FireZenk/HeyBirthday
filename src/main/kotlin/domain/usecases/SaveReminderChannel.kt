package domain.usecases

import domain.repositories.DiscordRepository

class SaveReminderChannel(private val repository: DiscordRepository) {

    fun execute(reminderChannel: String) = repository.saveReminderChannel(reminderChannel)
}