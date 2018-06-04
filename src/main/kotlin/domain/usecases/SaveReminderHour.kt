package domain.usecases

import domain.repositories.DiscordRepository

class SaveReminderHour(private val repository: DiscordRepository) {

    fun execute(reminderHour: String) = repository.saveReminderHour(reminderHour)
}