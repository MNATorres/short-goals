package com.mnatorres.shortgoals.app.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnatorres.shortgoals.app.data.GoalsRepository
import com.mnatorres.shortgoals.core.DailyCheck
import com.mnatorres.shortgoals.core.DayClose
import com.mnatorres.shortgoals.core.Goal
import com.mnatorres.shortgoals.core.currentStreak
import com.mnatorres.shortgoals.core.restingOn
import com.mnatorres.shortgoals.core.scheduledOn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TodayItem(
    val goal: Goal,
    val done: Boolean,
    val streak: Int,
)

data class TodayUiState(
    val date: LocalDate,
    val isToday: Boolean = true,
    val items: List<TodayItem> = emptyList(),
    val resting: List<Goal> = emptyList(),
    val doneCount: Int = 0,
    val close: DayClose? = null,
    val hasGoals: Boolean = false,
)

class TodayViewModel(
    private val repository: GoalsRepository,
    private val today: () -> LocalDate = LocalDate::now,
) : ViewModel() {

    private val selectedDate = MutableStateFlow(today())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TodayUiState> = selectedDate
        .flatMapLatest { date ->
            val month = YearMonth.from(date)
            combine(
                repository.goals(month),
                repository.checks(month),
                repository.closes(month),
            ) { goals, checks, closes -> buildState(date, goals, checks, closes) }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            TodayUiState(date = today()),
        )

    private fun buildState(
        date: LocalDate,
        goals: List<Goal>,
        checks: List<DailyCheck>,
        closes: List<DayClose>,
    ): TodayUiState {
        val scheduled = goals.scheduledOn(date)
        val doneByGoal = checks.filter { it.date == date }.associate { it.goalId to it.done }
        return TodayUiState(
            date = date,
            isToday = date == today(),
            items = scheduled.map { goal ->
                TodayItem(
                    goal = goal,
                    done = doneByGoal[goal.id] == true,
                    streak = currentStreak(goal, checks, date),
                )
            },
            resting = goals.restingOn(date),
            doneCount = scheduled.count { doneByGoal[it.id] == true },
            close = closes.firstOrNull { it.date == date },
            hasGoals = goals.any { !it.archived },
        )
    }

    /** Flips the mark for a goal on the shown date. No-op while the day is closed. */
    fun toggle(item: TodayItem) {
        val state = uiState.value
        if (state.close != null) return
        viewModelScope.launch {
            repository.setCheck(item.goal.id, state.date, !item.done)
        }
    }

    /** Steps one day back; every past day is editable. */
    fun previousDay() {
        selectedDate.update { it.minusDays(1) }
    }

    /** Steps one day forward, never beyond today. */
    fun nextDay() {
        selectedDate.update { if (it < today()) it.plusDays(1) else it }
    }

    /** Seals the shown date. Unmarked goals stay as not done; metrics don't change. */
    fun closeDay() {
        val date = uiState.value.date
        viewModelScope.launch { repository.closeDay(date, LocalDateTime.now()) }
    }

    /** Lifts the seal so the day can be corrected, to be closed again. */
    fun reopenDay() {
        val date = uiState.value.date
        viewModelScope.launch { repository.reopenDay(date) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
