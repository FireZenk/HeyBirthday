package ui.commands

import domain.models.Event
import domain.usecases.SaveReminderHour
import domain.usecases.SendMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ReminderHour(sendMessage: SendMessage, private val saveReminderHour: SaveReminderHour)
    : Command(sendMessage) {

    companion object {
        const val START_KEYWORD = "eb!reminderHour"
        const val END_RESPONSE = "Ok! I'll send reminders at"
        const val ERROR_FORMAT_RESPONSE = "The time format has to be 23:59"
        const val ERROR_SAVING_RESPONSE = "An error happened trying to save the reminder hour"
        private const val HOUR_REGEX_PATTERN = "^([01]\\d|2[0-3]):[0-5]\\d$"
    }

    override fun getLogger(): Logger = LoggerFactory.getLogger(ReminderHour::class.java)

    override fun processEvent(event: Event) = throw NotImplementedError()

    fun processEvent(event: Event, onConfigChanged: () -> Unit) {
        val rawReminderHour = event.message.substring(START_KEYWORD.length, event.message.length).trim()

        if (rawReminderHour.matches(Regex(HOUR_REGEX_PATTERN))) {
            saveReminderHour.execute(rawReminderHour).subscribe({
                onConfigChanged()
                sendResponse(event.channel, "$END_RESPONSE $rawReminderHour")
            }, {
                sendResponse(event.channel, ERROR_SAVING_RESPONSE)
            })
        } else {
            sendResponse(event.channel, ERROR_FORMAT_RESPONSE)
        }
    }
}