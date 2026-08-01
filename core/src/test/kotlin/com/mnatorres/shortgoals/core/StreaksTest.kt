package com.mnatorres.shortgoals.core

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals

class StreaksTest {

    private val july = YearMonth.of(2026, 7)
    private val daily = Goal(1, "No fumar", july, DayOfWeek.entries.toSet())
    private val wednesdays = Goal(3, "Ir al gimnasio", july, setOf(DayOfWeek.WEDNESDAY))

    private fun checks(goal: Goal, vararg days: Int, done: Boolean = true) =
        days.map { DailyCheck(goal.id, july.atDay(it), done) }

    // July 2026: Wednesdays fall on the 1st, 8th, 15th, 22nd and 29th.

    @Test
    fun `consecutive done marks build the streak`() {
        val c = checks(daily, 25, 26, 27)
        assertEquals(3, currentStreak(daily, c, july.atDay(27)))
    }

    @Test
    fun `today pending holds the streak instead of breaking it`() {
        val c = checks(daily, 25, 26, 27)
        assertEquals(3, currentStreak(daily, c, july.atDay(28)))
    }

    @Test
    fun `today done extends the streak`() {
        val c = checks(daily, 25, 26, 27, 28)
        assertEquals(4, currentStreak(daily, c, july.atDay(28)))
    }

    @Test
    fun `today marked not-done resets the streak`() {
        val c = checks(daily, 25, 26, 27) + checks(daily, 28, done = false)
        assertEquals(0, currentStreak(daily, c, july.atDay(28)))
    }

    @Test
    fun `a not-done mark in the past breaks the run`() {
        val c = checks(daily, 25, 27) + checks(daily, 26, done = false)
        assertEquals(1, currentStreak(daily, c, july.atDay(28)))
    }

    @Test
    fun `an unmarked past day breaks the run`() {
        val c = checks(daily, 24, 25, 27) // the 26th has no check at all
        assertEquals(1, currentStreak(daily, c, july.atDay(28)))
    }

    @Test
    fun `rest days never break a weekday-scheduled streak`() {
        // Wednesdays done on the 1st, 8th, 15th and 22nd; asOf Tuesday the 28th.
        val c = checks(wednesdays, 1, 8, 15, 22)
        assertEquals(4, currentStreak(wednesdays, c, july.atDay(28)))
    }

    @Test
    fun `missing a scheduled wednesday breaks the run`() {
        val c = checks(wednesdays, 1, 15, 22) // the 8th was missed
        assertEquals(2, currentStreak(wednesdays, c, july.atDay(28)))
    }

    @Test
    fun `no checks means no streak`() {
        assertEquals(0, currentStreak(daily, emptyList(), july.atDay(28)))
    }

    @Test
    fun `checks of other goals are ignored`() {
        val c = checks(daily, 27)
        assertEquals(0, currentStreak(wednesdays, c, july.atDay(28)))
    }

    @Test
    fun `best streak finds the longest past run`() {
        // Done 1-5, missed the 6th, done 7-15: best run is 9.
        val c = checks(daily, *(1..5).toList().toIntArray()) +
            checks(daily, *(7..15).toList().toIntArray())
        assertEquals(9, bestStreak(daily, c, july.atDay(20)))
    }

    @Test
    fun `best streak ignores occurrences after asOf`() {
        val c = checks(daily, *(1..15).toList().toIntArray())
        assertEquals(10, bestStreak(daily, c, july.atDay(10)))
    }
}
