package ui.commands

import domain.models.BirthdayNotExistsError
import domain.models.Event
import domain.usecases.DeleteBirthday
import domain.usecases.SendMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RemoveBirthday(sendMessage: SendMessage, private val deleteBirthday: DeleteBirthday) : Command(sendMessage) {

    companion object {
        const val START_KEYWORD = "eb!remove"
        const val END_RESPONSE = "Ok! I'll not remind this birthday"
        const val ERROR_NOT_EXISTS_RESPONSE = "This birthday does not exists"
        const val ERROR_SAVING_RESPONSE = "An error happened trying to remove the date"
    }

    override fun getLogger(): Logger = LoggerFactory.getLogger(RemoveBirthday::class.java)

    override fun processEvent(event: Event) {
        val rawName = event.message.substring(START_KEYWORD.length, event.message.length).trim()

        deleteBirthday.execute(rawName).subscribe({
            sendResponse(event.channel, END_RESPONSE)
        }, {
            when (it) {
                is BirthdayNotExistsError -> sendResponse(event.channel, ERROR_NOT_EXISTS_RESPONSE)
                else -> sendResponse(event.channel, ERROR_SAVING_RESPONSE)
            }
        })
    }
}