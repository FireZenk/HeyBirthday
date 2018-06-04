package domain.usecases

import domain.models.Birthday
import domain.repositories.DiscordRepository
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import java.text.SimpleDateFormat
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
        val dateFormatter = SimpleDateFormat("HH:mm:ss")
        val date = dateFormatter.parse("${repository.getReminderHour()}:00")

        Timer().schedule(object : TimerTask() {
            override fun run() {
                publisher.onNext(repository.haveBirthdaysToday())
            }

        }, date, dayInMillis)
    }
}