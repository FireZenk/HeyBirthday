package domain.usecases

import domain.repositories.DiscordRepository

class SaveReminderHour(private val repository: DiscordRepository) {

    fun execute(serverId: Long, reminderHour: String) = repository.saveReminderHour(serverId, reminderHour)
}