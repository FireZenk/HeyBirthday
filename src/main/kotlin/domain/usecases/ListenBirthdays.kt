package domain.usecases

import domain.models.Birthday
import domain.repositories.DiscordRepository
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import java.util.*

class ListenBirthdays(private val repository: DiscordRepository) {

    companion object {
        private const val dayInMillis = 86400000L
    }

    private val publisher: PublishProcessor<List<Birthday>> = PublishProcessor.create()
    private val flowable = publisher.onBackpressureLatest()

    fun execute(serverId: Long): Flowable<List<Birthday>> {
        scheduleTimer(serverId)
        return flowable
    }

    private fun scheduleTimer(serverId: Long) {
        val targetHour = repository.getReminderHour(serverId)
        val separator = targetHour.indexOf(":")
        val cal = Calendar.getInstance().apply {
            time = Date()
            this[Calendar.HOUR_OF_DAY] = targetHour.substring(0, separator).toInt()
            this[Calendar.MINUTE] = targetHour.substring(separator + 1, targetHour.length).toInt()
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                publisher.onNext(repository.haveBirthdaysToday(serverId))
            }

        }, cal.time, dayInMillis)
    }
}