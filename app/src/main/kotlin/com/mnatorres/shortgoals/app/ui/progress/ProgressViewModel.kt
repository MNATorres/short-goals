package com.mnatorres.shortgoals.app.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnatorres.shortgoals.app.data.GoalsRepository
import com.mnatorres.shortgoals.core.DailyCheck
import com.mnatorres.shortgoals.core.DayClose
import com.mnatorres.shortgoals.core.Goal
import com.mnatorres.shortgoals.core.HeatCell
import com.mnatorres.shortgoals.core.currentStreak
import com.mnatorres.shortgoals.core.monthHeatmap
import com.mnatorres.shortgoals.core.monthMetrics
import com.mnatorres.shortgoals.core.scheduledDates
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class GoalProgress(
    val goal: Goal,
    val done: Int,
    val scheduled: Int,
    val rate: Int,
    val streak: Int,
    /** Done/not-done for the goal's last scheduled occurrences, oldest first. */
    val spark: List<Boolean>,
)

data class ProgressUiState(
    val month: YearMonth,
    val done: Int = 0,
    val scheduled: Int = 0,
    val rate: Int = 0,
    val perfectDays: Int = 0,
    val heatmap: List<HeatCell> = emptyList(),
    val goals: List<GoalProgress> = emptyList(),
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
        repository.closes(month),
    ) { goals, checks, closes -> buildState(goals, checks, closes) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            ProgressUiState(month),
        )

    private fun buildState(
        goals: List<Goal>,
        checks: List<DailyCheck>,
        closes: List<DayClose>,
    ): ProgressUiState {
        val asOf = today()
        val metrics = monthMetrics(goals, checks, month, asOf)
        val doneDates = checks.filter { it.done }
            .groupBy({ it.goalId }, { it.date })
            .mapValues { (_, dates) -> dates.toSet() }
        return ProgressUiState(
            month = month,
            done = metrics.done,
            scheduled = metrics.scheduled,
            rate = metrics.rate,
            perfectDays = metrics.perfectDays,
            heatmap = monthHeatmap(goals, checks, closes, month, asOf),
            goals = metrics.perGoal.map { goalMetrics ->
                val goal = goalMetrics.goal
                GoalProgress(
                    goal = goal,
                    done = goalMetrics.done,
                    scheduled = goalMetrics.scheduled,
                    rate = goalMetrics.rate,
                    streak = currentStreak(goal, checks, asOf),
                    spark = goal.scheduledDates()
                        .filter { it <= asOf }
                        .takeLast(SPARK_OCCURRENCES)
                        .map { it in doneDates[goal.id].orEmpty() },
                )
            },
            hasGoals = goals.any { !it.archived },
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
        const val SPARK_OCCURRENCES = 14
    }
}
