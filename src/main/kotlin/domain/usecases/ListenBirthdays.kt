package domain.usecases

import domain.models.Birthday
import domain.repositories.DiscordRepository
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ListenBirthdays(private val repository: DiscordRepository) {

    companion object {
        private const val TIME_INTERVAL = 1L
        private val TIME_UNIT = TimeUnit.DAYS
    }

    fun execute(): Observable<List<Birthday>> = Observable.timer(TIME_INTERVAL, TIME_UNIT)
            .map { repository.haveBirthdaysToday() }
}