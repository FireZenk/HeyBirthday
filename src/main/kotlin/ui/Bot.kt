package ui

import domain.models.Birthday
import domain.models.Event
import io.reactivex.disposables.Disposable
import org.slf4j.LoggerFactory
import ui.commands.AddBirthday
import ui.commands.ReminderChannel
import ui.commands.ReminderHour
import ui.commands.RemoveBirthday
import java.util.*

object Bot {

    private val logger = LoggerFactory.getLogger(Bot::class.java)
    private lateinit var dependencies: DependencyTree
    private lateinit var listenBirthdaysDisposable: Disposable

    @JvmStatic fun main(args: Array<String>) {
        dependencies = DependencyTree(args[0], args[1])

        listenMessages()
        listenBirthdays()
    }

    private fun listenMessages() {
        dependencies.listenMessages
                .execute()
                .subscribe(
                        { processEvent(it) },
                        { logger.debug("Discord api error", it) })
    }

    private fun listenBirthdays() {
        listenBirthdaysDisposable = dependencies.listenBirthdays
                .execute()
                .subscribe(
                        {
                            it.forEach { processBirthday(it) }
                            processBirthday(Birthday("Nayeon", Date())) // todo test only
                        },
                        { logger.debug("Discord api error", it) })
    }

    private fun processEvent(it: Event) {
        when {
            it.message.startsWith(AddBirthday.START_KEYWORD, true)
            -> dependencies.addBirthday.processEvent(it)
            it.message.startsWith(RemoveBirthday.START_KEYWORD, true)
            -> dependencies.removeBirthday.processEvent(it)
            it.message.startsWith(ReminderChannel.START_KEYWORD, true)
            -> dependencies.reminderChannel.processEvent(it)
            it.message.startsWith(ReminderHour.START_KEYWORD, true)
            -> dependencies.reminderHour.processEvent(it, {
                listenBirthdaysDisposable.dispose()
                listenBirthdays()
            })
        }
    }

    private fun processBirthday(it: Birthday) {
        dependencies.celebrateBirthday
                .execute(it.name, "Today is ${it.name}'s birthday! \uD83C\uDF89\uD83C\uDF89")
                .subscribe({}, { logger.debug("Discord api error", it) })
    }
}