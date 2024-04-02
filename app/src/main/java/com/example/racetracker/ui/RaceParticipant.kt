package com.example.racetracker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * This class represents a state holder for race participant.
 *
 * Этот класс соответствует статусу участника гонки.
 */
class RaceParticipant(
    val name: String, // имя игрока
    val maxProgress: Int = 100, // максимальный прогресс в процентах
    val progressDelayMillis: Long = 500L, // задержка 0.5 сек
    private val progressIncrement: Int = 1, // значение по умолчанию progressIncrement равно 1
    private val initialProgress: Int = 0 // инициализация прогреса, изначально он нулевой
) {
    init {
        require(maxProgress > 0) { "maxProgress=$maxProgress; must be > 0" }
        require(progressIncrement > 0) { "progressIncrement=$progressIncrement; must be > 0" }
    }

    /**
     * Indicates the race participant's current progress
     *
     * Показывает текущий прогресс участника гонки
     */
    var currentProgress by mutableIntStateOf(initialProgress) // Значение currentProgress установлено равным initialProgress, что 0 равно
        private set

    /**
     * Updates the value of [currentProgress] by value [progressIncrement] until it reaches
     * [maxProgress]. There is a delay of [progressDelayMillis] between each update.
     *
     * Обновляет значение [currentProgress] на значение [progressIncrement] до тех пор, пока оно
     * не достигнет значения [maxProgress]. Между каждым обновлением существует
     * задержка [progressDelayMillis].
     */
    suspend fun run() { // так как в ф-ия run вызывает dalay() ф-ию, нужно подставить suspend
        while (currentProgress < maxProgress) { // while цикл, который выполняется до тех пор, пока currentProgress не будет достигнуто значение maxProgress, которое установлено равным 100
            delay(progressDelayMillis) // добавляет задержку между приращениями прогресса, имитации различных интервалов выполнения в гонке
            currentProgress += progressIncrement // Чтобы имитировать прогресс участника, увеличеваем значение currentProgress на значение progressIncrement
        }
    }

    /**
     * Regardless of the value of [initialProgress] the reset function will reset the
     * [currentProgress] to 0
     *
     * Независимо от значения [initialProgress] функция сброса
     * сбросит значение [currentProgress] на 0
     */
    fun reset() {
        currentProgress = 0
    }
}

/**
 * The Linear progress indicator expects progress value in the range of 0-1. This property
 * calculate the progress factor to satisfy the indicator requirements.
 *
 * Линейный индикатор прогресса рассчитывает значение прогресса в диапазоне от 0 до 1. Это свойство
 * позволяет рассчитать коэффициент прогресса в соответствии с требованиями к индикатору.
 */
val RaceParticipant.progressFactor: Float
    get() = currentProgress / maxProgress.toFloat()
