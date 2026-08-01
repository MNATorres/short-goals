package com.mnatorres.shortgoals.core

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

/**
 * A goal the user commits to for one month, on a fixed set of weekdays.
 * Goals are archived rather than deleted so past checks keep their meaning.
 */
data class Goal(
    val id: Long,
    val name: String,
    val month: YearMonth,
    val weekdays: Set<DayOfWeek>,
    val archived: Boolean = false,
) {
    init {
        require(name.isNotBlank()) { "Goal name must not be blank" }
        require(weekdays.isNotEmpty()) { "A goal must be scheduled on at least one weekday" }
    }
}

/**
 * The user's mark for one goal on one date. The absence of a check for a
 * scheduled occurrence means "not done" — metrics never treat it as pending.
 */
data class DailyCheck(
    val goalId: Long,
    val date: LocalDate,
    val done: Boolean,
)

/**
 * Records that the user explicitly closed (sealed) a day, and when.
 * Closing never changes metrics; it only marks the day as reviewed.
 */
data class DayClose(
    val date: LocalDate,
    val closedAt: LocalDateTime,
)
