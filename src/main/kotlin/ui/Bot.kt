package ui

import domain.models.Birthday
import domain.models.ConnectionSate
import domain.models.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import org.slf4j.LoggerFactory
import ui.commands.*
import java.time.LocalDate

object Bot {

    private val logger = LoggerFactory.getLogger(Bot::class.java)
    private lateinit var dependencies: DependencyTree

    private val serverDisposables = mutableMapOf<Long, Disposable>()
    private val listenBirthdaysDisposables = CompositeDisposable()

    @JvmStatic fun main(args: Array<String>) {
        dependencies = DependencyTree(args[0], args[1])

        listenServerConnection()
        listenMessages()
    }

    private fun listenServerConnection() {
        dependencies.listenServerConnection
                .execute()
                .subscribe(
                        {
                            when(it.state) {
                                is ConnectionSate.Available -> listenBirthdays(it.serverId)
                                is ConnectionSate.Unavailable -> disposeBirthdaysListener(it.serverId)
                            }
                        },
                        { logger.debug("Discord api error", it) })
    }

    private fun listenMessages() {
        dependencies.listenMessages
                .execute()
                .subscribe(
                        { processEvent(it) },
                        { logger.debug("Discord api error", it) })
    }

    private fun listenBirthdays(serverId: Long) {
        val disposable = dependencies.listenBirthdays
                .execute(serverId)
                .subscribe(
                        { it.forEach { processBirthday(it) } },
                        { logger.debug("Discord api error", it) })

        serverDisposables.put(serverId, disposable)
        listenBirthdaysDisposables += disposable
    }

    private fun disposeBirthdaysListener(serverId: Long) {
        serverDisposables[serverId]?.run {
            serverDisposables.remove(serverId)
            listenBirthdaysDisposables.delete(this)
        }
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
                disposeBirthdaysListener(it.serverId)
                listenBirthdays(it.serverId)
            })
            it.message.startsWith(Info.START_KEYWORD, true)
            -> dependencies.info.processEvent(it)
        }
    }

    private fun processBirthday(it: Birthday) {
        val yearsOld = LocalDate.now().year - it.date.year
        dependencies.celebrateBirthday
                .execute(it.serverId, it.name, "Today is ${it.name}'s birthday ($yearsOld)! \uD83C\uDF89\uD83C\uDF89")
                .subscribe({}, { logger.debug("Discord api error", it) })
    }
}