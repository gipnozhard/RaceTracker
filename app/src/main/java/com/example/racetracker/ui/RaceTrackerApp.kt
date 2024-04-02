package com.example.racetracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.racetracker.R
import com.example.racetracker.ui.theme.RaceTrackerTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun RaceTrackerApp() {
    /**
     * Note: To survive the configuration changes such as screen rotation, [rememberSaveable] should
     * be used with custom Saver object. But to keep the example simple, and keep focus on
     * Coroutines that implementation detail is stripped out.
     *
     * Примечание: Чтобы избежать изменений конфигурации, таких как поворот экрана, [rememberSaveable] следует
     * использовать с пользовательским объектом сохранения. Но чтобы упростить пример и сосредоточить внимание на
     * сопрограммах, детали реализации которых удалены.
     */
    val playerOne = remember { // игрок1
        RaceParticipant(name = "Player 1", progressIncrement = 1) //значение по умолчанию 1
    }
    val playerTwo = remember { // игрок2
        RaceParticipant(name = "Player 2", progressIncrement = 2) //значение по умолчанию 2 , значит игрок два быстрее на раз
    }
    var raceInProgress by remember { mutableStateOf(false) } // прогресс гонки изначально false

    if (raceInProgress) {
        LaunchedEffect(playerOne, playerTwo) {// запуск двух потоков
            coroutineScope {
                launch { playerOne.run() }
                launch { playerTwo.run() }
            }
            raceInProgress = false // прогресс гонки false
        }
    }
    RaceTrackerScreen(
        playerOne = playerOne, // игрок1
        playerTwo = playerTwo, // игрок2
        isRunning = raceInProgress, // прогресс
        onRunStateChange = { raceInProgress = it }, // прогресс
        modifier = Modifier // модификатор
            .statusBarsPadding()
            .fillMaxSize() // занять максимальный размер контейнера
            .verticalScroll(rememberScrollState()) // вертикальный скролинг
            .safeDrawingPadding()
            .padding(horizontal = dimensionResource(R.dimen.padding_medium)), // отступ по горизонту
    )
}

@Composable
private fun RaceTrackerScreen( // главный экран UI приложения
    playerOne: RaceParticipant, // игрок1
    playerTwo: RaceParticipant, // игрок1
    isRunning: Boolean,
    onRunStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier // модификатор
) {
    Column( // контейнер список
        modifier = modifier, // модификатор
        verticalArrangement = Arrangement.Center, // расположение по вертикали в центре
        horizontalAlignment = Alignment.CenterHorizontally // расположение по горизонтали в центре
    ) {
        Text( // верхний заголовок приложения
            text = stringResource(R.string.run_a_race), // сам текст взятый из рессурсов string
            style = MaterialTheme.typography.headlineSmall, // стиль шрифта
        )
        Column( // контейнер список
            modifier = Modifier // модификатор
                .fillMaxSize() // занимать полный размер контейнера
                .padding(dimensionResource(R.dimen.padding_medium)), // отступ со всех сторон
            verticalArrangement = Arrangement.Center, // расположение по вертикали в центре
            horizontalAlignment = Alignment.CenterHorizontally, // расположение по горизонтали в центре
        ) {
            Icon( // иконка контейнер
                painter = painterResource(R.drawable.ic_walk), // иконка ресурс
                contentDescription = null, // описания нету
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)), // отступ со всех сторон
            )
            StatusIndicator( // статус индикатор 1 игрока
                participantName = playerOne.name, // имя игрока1
                currentProgress = playerOne.currentProgress, // текущий прогресс
                maxProgress = stringResource( // максимальный прогресс
                    R.string.progress_percentage,
                    playerOne.maxProgress
                ),
                progressFactor = playerOne.progressFactor, // значение прогресса вычисленное
                modifier = Modifier.fillMaxWidth() // модификатор, занять полную ширину контейнера
            )
            Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_large))) // пропуск между контейнерами
            StatusIndicator( // статус индикатор 2 игрока
                participantName = playerTwo.name, // имя игрока2
                currentProgress = playerTwo.currentProgress, // текущий прогресс
                maxProgress = stringResource( // максимальный прогресс
                    R.string.progress_percentage,
                    playerTwo.maxProgress
                ),
                progressFactor = playerTwo.progressFactor, // значение прогресса вычисленное
                modifier = Modifier.fillMaxWidth(), // модификатор, занять полную ширину контейнера
            )
            Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_large))) // пропуск между контейнерами
            RaceControls( // управление программой
                isRunning = isRunning,
                onRunStateChange = onRunStateChange,
                onReset = {
                    playerOne.reset()
                    playerTwo.reset()
                    onRunStateChange(false)
                },
                modifier = Modifier.fillMaxWidth(), // модификатор, занять всю ширину контейнера
            )
        }
    }
}

@Composable
private fun StatusIndicator( // статус индикатор
    participantName: String, // имя участника стринг
    currentProgress: Int, // текущий прогресс инт
    maxProgress: String, // максимальный прокресс стринг
    progressFactor: Float, // значение прогресса вычисленное
    modifier: Modifier = Modifier // модификатор
) {
    Row( // ряд контейнер
        modifier = modifier // модификатор
    ) {
        Text( // текст
            text = participantName, // имя участника
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small)) // отступ справа
        )
        Column( // список контейнер
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)) // вертикальный пробел между контейнерами списка
        ) {
            LinearProgressIndicator( // линия прогресса
                progress = { progressFactor }, // значение прогресса
                modifier = Modifier // модификатор
                    .fillMaxWidth() // занимать всю ширину контейнера
                    .height(dimensionResource(R.dimen.progress_indicator_height)) // высота контейнера
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.progress_indicator_corner_radius))) // обрезание округленным контуром
            )
            Row( // ряд контейнер
                modifier = Modifier.fillMaxWidth(), // модификатор, занять всю ширину контейнера
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text( // текст1 в ряду , (контейнер)
                    text = stringResource(R.string.progress_percentage, currentProgress), // размер текущего прогресса
                    textAlign = TextAlign.Start, // текс занимает начало контейнера
                    modifier = Modifier.weight(1f) // модификатор, вес значения в контейнере ряд
                )
                Text( // текс2 в ряду , (контейнер)
                    text = maxProgress, // значение максимального прогресса
                    textAlign = TextAlign.End, // текст занимает конец контейнера
                    modifier = Modifier.weight(1f) // модификатор, вес значения в контейнере ряд
                )
            }
        }
    }
}

@Composable
private fun RaceControls( // управление программой (кнопки)
    onRunStateChange: (Boolean) -> Unit,
    onReset: () -> Unit, // сброс
    modifier: Modifier = Modifier, // модификатор
    isRunning: Boolean = true, // значение пуска забега, конструктор тру
) {
    Column( // список контейнер
        modifier = modifier.padding(top = dimensionResource(R.dimen.padding_medium)), // модификатор, отступ сверху
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)) // вертикальное положение, пропуск между контейнерами
    ) {
        Button( // кнопка контейнер
            onClick = { onRunStateChange(!isRunning) }, //изначально onRunStateChange = false, а
            modifier = Modifier.fillMaxWidth(), // модификатор, занять всю ширину контейнера
        ) {
            Text(if (isRunning) stringResource(R.string.pause) else stringResource(R.string.start)) // текс кнопки (если пуска забега, то меняется кнопка на паузу, иначе старт)
        }
        OutlinedButton( // выделенная кнопка с контуром
            onClick = onReset, // сброс при нажатии
            modifier = Modifier.fillMaxWidth(), // модификатор, занять всю ширину контейнера
        ) {
            Text(stringResource(R.string.reset)) // текс кнопки
        }
    }
}

@Preview(showBackground = true) // превьюшка
@Composable
fun RaceTrackerAppPreview() {
    RaceTrackerTheme {
        RaceTrackerApp()
    }
}
