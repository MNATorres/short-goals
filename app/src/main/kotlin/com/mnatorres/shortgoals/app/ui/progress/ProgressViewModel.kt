package com.mnatorres.shortgoals.app.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnatorres.shortgoals.app.data.GoalsRepository
import com.mnatorres.shortgoals.core.DailyCheck
import com.mnatorres.shortgoals.core.Goal
import com.mnatorres.shortgoals.core.monthMetrics
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProgressUiState(
    val month: YearMonth,
    val done: Int = 0,
    val scheduled: Int = 0,
    val rate: Int = 0,
    val perfectDays: Int = 0,
    val hasGoals: Boolean = false,
)

class ProgressViewModel(
    private val repository: GoalsRepository,
    private val today: () -> LocalDate = LocalDate::now,
) : ViewModel() {

    private val month = YearMonth.from(today())

    val uiState: StateFlow<ProgressUiState> = combine(
        repository.goals(month),
        repository.checks(month),
    ) { goals, checks -> buildState(goals, checks) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            ProgressUiState(month),
        )

    private fun buildState(
        goals: List<Goal>,
        checks: List<DailyCheck>,
    ): ProgressUiState {
        val metrics = monthMetrics(goals, checks, month, today())
        return ProgressUiState(
            month = month,
            done = metrics.done,
            scheduled = metrics.scheduled,
            rate = metrics.rate,
            perfectDays = metrics.perfectDays,
            hasGoals = goals.any { !it.archived },
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
