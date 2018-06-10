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

    fun execute(): Flowable<List<Birthday>> {
        scheduleTimer()
        return flowable
    }

    private fun scheduleTimer() {
        val targetHour = repository.getReminderHour()
        val separator = targetHour.indexOf(":")
        val cal = Calendar.getInstance().apply {
            time = Date()
            this[Calendar.HOUR_OF_DAY] = targetHour.substring(0, separator).toInt()
            this[Calendar.MINUTE] = targetHour.substring(separator + 1, targetHour.length).toInt()
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                publisher.onNext(repository.haveBirthdaysToday())
            }

        }, cal.time, dayInMillis)
    }
}