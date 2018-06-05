package ui.commands

import domain.models.BirthdayDuplicatedError
import domain.models.Event
import domain.usecases.SaveBirthday
import domain.usecases.SendMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class AddBirthday(sendMessage: SendMessage, private val saveBirthday: SaveBirthday) : Command(sendMessage) {

    companion object {
        private const val DATE_FORMAT = "MM-dd-yyyy"

        const val START_KEYWORD = "eb!add"
        const val END_RESPONSE = "Ok! I'll send a reminder that day!"
        const val ERROR_FORMAT_RESPONSE = "The date format has to be $DATE_FORMAT"
        const val ERROR_DUPLICATED_RESPONSE = "This birthday already exists"
        const val ERROR_SAVING_RESPONSE = "An error happened trying to save the date"
    }

    override fun getLogger(): Logger = LoggerFactory.getLogger(AddBirthday::class.java)

    override fun processEvent(event: Event) {
        val rawNameAndDate = event.message.substring(START_KEYWORD.length, event.message.length).trim()

        val name = rawNameAndDate.substring(0, rawNameAndDate.indexOf(" "))
        val rawDate = rawNameAndDate.substring(rawNameAndDate.indexOf(" ") + 1, rawNameAndDate.length)

        val format = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.ENGLISH)

        try {
            val date = LocalDate.parse(rawDate, format)

            saveBirthday.execute(event.serverId, name, date).subscribe({
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
}