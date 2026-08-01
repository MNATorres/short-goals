package com.mnatorres.shortgoals.app.data

import com.mnatorres.shortgoals.core.DailyCheck
import com.mnatorres.shortgoals.core.DayClose
import com.mnatorres.shortgoals.core.Goal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The app's single data access point: month-windowed Flows of domain
 * objects and suspend writes. Screens combine these with the :core
 * metric functions; nothing derived is ever stored.
 */
class GoalsRepository(private val db: ShortGoalsDatabase) {

    fun goals(month: YearMonth): Flow<List<Goal>> =
        db.goalDao().forMonth(month).map { goals -> goals.map { it.toDomain() } }

    fun checks(month: YearMonth): Flow<List<DailyCheck>> =
        db.dailyCheckDao().between(month.atDay(1), month.atEndOfMonth())
            .map { checks -> checks.map { it.toDomain() } }

    fun closes(month: YearMonth): Flow<List<DayClose>> =
        db.dayCloseDao().between(month.atDay(1), month.atEndOfMonth())
            .map { closes -> closes.map { it.toDomain() } }

    suspend fun addGoal(name: String, month: YearMonth, weekdays: Set<DayOfWeek>): Long =
        db.goalDao().insert(GoalEntity(name = name, month = month, weekdays = weekdays))

    suspend fun updateGoal(goal: Goal) = db.goalDao().update(goal.toEntity())

    suspend fun archiveGoal(goal: Goal) = updateGoal(goal.copy(archived = true))

    suspend fun setCheck(goalId: Long, date: LocalDate, done: Boolean) =
        db.dailyCheckDao().upsert(DailyCheckEntity(goalId, date, done))

    suspend fun closeDay(date: LocalDate, closedAt: LocalDateTime) =
        db.dayCloseDao().upsert(DayCloseEntity(date, closedAt))

    suspend fun reopenDay(date: LocalDate) = db.dayCloseDao().delete(date)
}
