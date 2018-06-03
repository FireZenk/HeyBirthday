package ui.commands

import domain.models.BirthdayDuplicatedError
import domain.models.Event
import domain.usecases.SaveBirthday
import domain.usecases.SendMessage
import org.javacord.api.entity.channel.TextChannel
import org.slf4j.LoggerFactory
import java.text.ParseException
import java.util.Locale
import java.text.SimpleDateFormat

class AddBirthday(private val sendMessage: SendMessage, private val saveBirthday: SaveBirthday) {

    companion object {
        private const val DATE_FORMAT = "MM-dd-yyyy"

        const val START_KEYWORD = "eb!add"
        const val END_RESPONSE = "Ok! I'll send a reminder that day!"
        const val ERROR_FORMAT_RESPONSE = "The date format has to be $DATE_FORMAT"
        const val ERROR_DUPLICATED_RESPONSE = "This birthday already exists"
        const val ERROR_SAVING_RESPONSE = "An error happen trying to save the date"
    }

    private val logger = LoggerFactory.getLogger(AddBirthday::class.java)

    fun processEvent(event: Event) {
        val rawNameAndDate = event.message.substring(START_KEYWORD.length, event.message.length).trim()

        val name = rawNameAndDate.substring(0, rawNameAndDate.indexOf(" "))
        val rawDate = rawNameAndDate.substring(rawNameAndDate.indexOf(" "), rawNameAndDate.length)

        val format = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)

        try {
            val date = format.parse(rawDate)

            saveBirthday.execute(name, date).subscribe({
                sendResponse(event.channel, END_RESPONSE)
            }, {
                when (it) {
                    is BirthdayDuplicatedError -> sendResponse(event.channel, ERROR_DUPLICATED_RESPONSE)
                    else -> sendResponse(event.channel, ERROR_SAVING_RESPONSE)
                }
            })
        } catch (e: ParseException) {
            sendResponse(event.channel, ERROR_FORMAT_RESPONSE)
        }
    }

    private fun sendResponse(channel: TextChannel, message: String) {
        sendMessage.execute(channel, message)
                .subscribe({}, { logger.debug("Discord api error", it) })
    }
}