package com.mnatorres.shortgoals.core

import java.time.LocalDate
import java.time.YearMonth

/** A goal's completion within its month, up to a reference date. */
data class GoalMetrics(
    val goal: Goal,
    val done: Int,
    val scheduled: Int,
) {
    /** Whole percentage, 0 when nothing was scheduled yet. */
    val rate: Int get() = if (scheduled == 0) 0 else done * 100 / scheduled
}

/** The month's aggregate completion, up to a reference date. */
data class MonthMetrics(
    val done: Int,
    val scheduled: Int,
    val perfectDays: Int,
    val perGoal: List<GoalMetrics>,
) {
    /** Whole percentage, 0 when nothing was scheduled yet. */
    val rate: Int get() = if (scheduled == 0) 0 else done * 100 / scheduled
}

/**
 * Metrics for [month], counting only scheduled occurrences from the start
 * of the month through [asOf] inclusive — future occurrences don't lower
 * the rate. An unmarked past occurrence counts as not done; closing a day
 * never changes these numbers. A perfect day had at least one occurrence
 * scheduled and all of them done. Archived goals are excluded.
 */
fun monthMetrics(
    goals: List<Goal>,
    checks: List<DailyCheck>,
    month: YearMonth,
    asOf: LocalDate,
): MonthMetrics {
    val active = goals.filter { !it.archived && it.month == month }
    val doneByGoal = checks.groupBy { it.goalId }
        .mapValues { (_, list) -> list.associate { it.date to it.done } }

    val perGoal = active.map { goal ->
        val dates = goal.scheduledDates().filter { it <= asOf }
        val done = dates.count { doneByGoal[goal.id]?.get(it) == true }
        GoalMetrics(goal, done, dates.size)
    }

    val perfectDays = (1..month.lengthOfMonth())
        .map(month::atDay)
        .filter { it <= asOf }
        .count { date ->
            val scheduled = active.scheduledOn(date)
            scheduled.isNotEmpty() && scheduled.all { doneByGoal[it.id]?.get(date) == true }
        }

    return MonthMetrics(
        done = perGoal.sumOf { it.done },
        scheduled = perGoal.sumOf { it.scheduled },
        perfectDays = perfectDays,
        perGoal = perGoal,
    )
}
