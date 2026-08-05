package com.mnatorres.shortgoals.app.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnatorres.shortgoals.app.data.GoalsRepository
import com.mnatorres.shortgoals.core.Goal
import java.time.DayOfWeek
import java.time.YearMonth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GoalsUiState(
    val month: YearMonth,
    val active: List<Goal> = emptyList(),
    val archived: List<Goal> = emptyList(),
    val previousMonthGoals: List<Goal> = emptyList(),
) {
    /** A fresh month with nothing loaded yet, but last month had goals. */
    val showRepeatOffer: Boolean
        get() = active.isEmpty() && previousMonthGoals.isNotEmpty()
}

class GoalsViewModel(
    private val repository: GoalsRepository,
    private val month: YearMonth = YearMonth.now(),
) : ViewModel() {

    private val previousMonth = month.minusMonths(1)

    val uiState: StateFlow<GoalsUiState> = combine(
        repository.goals(month),
        repository.goals(previousMonth),
    ) { current, previous ->
        GoalsUiState(
            month = month,
            active = current.filter { !it.archived },
            archived = current.filter { it.archived },
            previousMonthGoals = previous.filter { !it.archived },
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            GoalsUiState(month),
        )

    /**
     * Copies last month's active goals into this month with fresh checks
     * and streaks. Only offered while this month is still empty.
     */
    fun repeatPreviousMonth() {
        val state = uiState.value
        if (state.active.isNotEmpty() || state.previousMonthGoals.isEmpty()) return
        viewModelScope.launch {
            state.previousMonthGoals.forEach { goal ->
                repository.addGoal(goal.name, month, goal.weekdays)
            }
        }
    }

    /** Creates a goal for this month. Silently ignores invalid input. */
    fun addGoal(name: String, weekdays: Set<DayOfWeek>) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || weekdays.isEmpty()) return
        viewModelScope.launch { repository.addGoal(trimmed, month, weekdays) }
    }

    /** Renames and/or reschedules a goal. Silently ignores invalid input. */
    fun updateGoal(goal: Goal, name: String, weekdays: Set<DayOfWeek>) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || weekdays.isEmpty()) return
        viewModelScope.launch {
            repository.updateGoal(goal.copy(name = trimmed, weekdays = weekdays))
        }
    }

    /** Archives instead of deleting so past checks keep their meaning. */
    fun archive(goal: Goal) {
        viewModelScope.launch { repository.archiveGoal(goal) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
