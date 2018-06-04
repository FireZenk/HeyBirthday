package ui.commands

import domain.models.Event
import domain.usecases.SaveReminderChannel
import domain.usecases.SendMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ReminderChannel(sendMessage: SendMessage, private val saveReminderChannel: SaveReminderChannel)
    : Command(sendMessage) {

    companion object {
        const val START_KEYWORD = "eb!reminderChannel"
        const val END_RESPONSE = "Ok! I'll send reminders to: #"
        const val ERROR_SAVING_RESPONSE = "An error happened trying to save the reminder channel"
    }

    override fun getLogger(): Logger = LoggerFactory.getLogger(ReminderChannel::class.java)

    override fun processEvent(event: Event) {
        val rawReminderChannel = event.message.substring(START_KEYWORD.length, event.message.length).trim()

        saveReminderChannel.execute(rawReminderChannel).subscribe({
            sendResponse(event.channel, "$END_RESPONSE$rawReminderChannel")
        }, {
            sendResponse(event.channel, ERROR_SAVING_RESPONSE)
        })
    }
}