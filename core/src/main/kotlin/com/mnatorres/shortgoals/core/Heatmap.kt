package com.mnatorres.shortgoals.core

import java.time.LocalDate
import java.time.YearMonth

/** Intensity bucket for a heatmap cell, relative to what that day scheduled. */
enum class HeatLevel { NONE, LOW, MEDIUM, FULL }

/**
 * One day of the month heatmap. [closed] mirrors the explicit day close so
 * the UI can mark days still pending review; it never affects the level.
 */
data class HeatCell(
    val date: LocalDate,
    val scheduled: Int,
    val done: Int,
    val closed: Boolean,
) {
    val level: HeatLevel
        get() = when {
            done == 0 -> HeatLevel.NONE
            done == scheduled -> HeatLevel.FULL
            done * 2 >= scheduled -> HeatLevel.MEDIUM
            else -> HeatLevel.LOW
        }
}

/**
 * A cell per day of [month] from the 1st through [asOf] (the whole month
 * when [asOf] is beyond it). A Sunday with 3 of 3 done paints as FULL just
 * like a Wednesday with 5 of 5: intensity is relative to that day's own
 * schedule. Days that scheduled nothing stay at NONE.
 */
fun monthHeatmap(
    goals: List<Goal>,
    checks: List<DailyCheck>,
    closes: List<DayClose>,
    month: YearMonth,
    asOf: LocalDate,
): List<HeatCell> {
    val doneByGoal = checks.groupBy { it.goalId }
        .mapValues { (_, list) -> list.associate { it.date to it.done } }
    val closedDates = closes.mapTo(mutableSetOf()) { it.date }

    return (1..month.lengthOfMonth())
        .map(month::atDay)
        .filter { it <= asOf }
        .map { date ->
            val scheduled = goals.scheduledOn(date)
            HeatCell(
                date = date,
                scheduled = scheduled.size,
                done = scheduled.count { doneByGoal[it.id]?.get(date) == true },
                closed = date in closedDates,
            )
        }
}
