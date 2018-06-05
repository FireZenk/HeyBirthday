package ui.commands

import domain.models.Event
import domain.usecases.SendMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Info(sendMessage: SendMessage) : Command(sendMessage) {

    companion object {
        const val START_KEYWORD = "eb!info"
        const val END_RESPONSE =
                "Here is the list of all available commands:\n\n" +
                "Configuration:\n" +
                "   **eb!reminderChannel** `channelName`\n" +
                "   **eb!reminderHour** `23:59`\n" +
                "Usage:\n" +
                "   **eb!add** `Name` `MM-dd-yyyy`\n" +
                "   **eb!remove** `Name`"
    }

    override fun getLogger(): Logger = LoggerFactory.getLogger(Info::class.java)

    override fun processEvent(event: Event) = sendResponse(event.channel, END_RESPONSE)
}