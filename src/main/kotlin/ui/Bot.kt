package ui

import domain.models.Birthday
import domain.models.Event
import org.slf4j.LoggerFactory
import ui.commands.AddBirthday
import ui.commands.ReminderChannel
import ui.commands.ReminderHour
import ui.commands.RemoveBirthday

object Bot {

    private val logger = LoggerFactory.getLogger(Bot::class.java)
    private lateinit var dependencies: DependencyTree

    @JvmStatic fun main(args: Array<String>) {
        dependencies = DependencyTree(args[0], args[1])

        dependencies.listenMessages
                .execute()
                .subscribe(
                        { processEvent(it) },
                        { logger.debug("Discord api error", it) })

        dependencies.listenBirthdays
                .execute()
                .subscribe(
                        { it.forEach { processBirthday(it) } },
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
            -> dependencies.reminderHour.processEvent(it)
        }
    }

    private fun processBirthday(it: Birthday) {
        dependencies.celebrateBirthday
                .execute(it.name, "Today is ${it.name}'s birthday! \uD83C\uDF89\uD83C\uDF89")
                .subscribe({}, { logger.debug("Discord api error", it) })
    }
}