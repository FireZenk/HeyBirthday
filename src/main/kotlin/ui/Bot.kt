package ui

import data.db.JsondbDataSource
import data.net.DiscordDataSource
import domain.models.Birthday
import domain.models.Event
import domain.repositories.DiscordRepository
import domain.usecases.*
import org.slf4j.LoggerFactory
import ui.commands.AddBirthday
import ui.commands.RemoveBirthday
import java.util.*

/**
 * Main bot file
 */
object Bot {

    private val logger = LoggerFactory.getLogger(Bot::class.java)

    private val repository: DiscordRepository by lazy {
        DiscordRepository(DiscordDataSource(token), JsondbDataSource())
    }

    private val listenMessages: ListenMessages by lazy { ListenMessages(repository) }
    private val listenBirthdays: ListenBirthdays by lazy { ListenBirthdays(repository) }
    private val sendMessage: SendMessage by lazy { SendMessage(repository) }
    private val saveBirthday by lazy { SaveBirthday(repository) }
    private val deleteBirthday by lazy { DeleteBirthday(repository) }
    private val celebrateBirthday by lazy { CelebrateBirthday(repository) }

    private lateinit var token: String

    // Commands
    private val addBirthday: AddBirthday by lazy { AddBirthday(sendMessage, saveBirthday) }
    private val removeBirthday: RemoveBirthday by lazy { RemoveBirthday(sendMessage, deleteBirthday) }

    @JvmStatic fun main(args: Array<String>) {
        token = args[0]

        listenMessages
                .execute()
                .subscribe(
                        { processEvent(it) },
                        { logger.debug("Discord api error", it) })

        listenBirthdays
                .execute()
                .subscribe(
                        { it.forEach { processBirthday(it) } },
                        { logger.debug("Discord api error", it) })

        // TODO TEST ONLY
        processBirthday(Birthday("Nayeon", Date()))
    }

    private fun processEvent(it: Event) {
        when {
            it.message.equals("nayeon", ignoreCase = true) -> {
                sendMessage.execute(it.channel, "Cute!")
                        .subscribe({}, { logger.debug("Discord api error", it) })
            }
            it.message.startsWith(AddBirthday.START_KEYWORD, true) -> addBirthday.processEvent(it)
            it.message.startsWith(RemoveBirthday.START_KEYWORD, true) -> removeBirthday.processEvent(it)
        }
    }

    private fun processBirthday(it: Birthday) {
        celebrateBirthday.execute("Today is ${it.name}'s birthday! \uD83C\uDF89\uD83C\uDF89")
                .subscribe({}, { logger.debug("Discord api error", it) })
    }
}