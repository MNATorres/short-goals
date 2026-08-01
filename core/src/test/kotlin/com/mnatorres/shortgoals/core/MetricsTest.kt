package com.mnatorres.shortgoals.core

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals

class MetricsTest {

    private val july = YearMonth.of(2026, 7)
    private val daily = Goal(1, "No fumar", july, DayOfWeek.entries.toSet())
    private val wednesdays = Goal(3, "Ir al gimnasio", july, setOf(DayOfWeek.WEDNESDAY))

    private fun done(goal: Goal, vararg days: Int) =
        days.map { DailyCheck(goal.id, july.atDay(it), true) }

    // July 2026: Wednesdays fall on the 1st, 8th and 15th within the first half.

    @Test
    fun `rates count only occurrences up to asOf`() {
        val goals = listOf(daily, wednesdays)
        val checks = done(daily, *(1..10).toList().toIntArray()) + done(wednesdays, 8, 15)
        val m = monthMetrics(goals, checks, july, july.atDay(15))

        // daily: 10 of 15; wednesdays: 2 of 3 (1st, 8th, 15th scheduled so far)
        assertEquals(15 + 3, m.scheduled)
        assertEquals(12, m.done)
        assertEquals(12 * 100 / 18, m.rate) // 66, integer percentage
        assertEquals(10, m.perGoal[0].done)
        assertEquals(15, m.perGoal[0].scheduled)
        assertEquals(66, m.perGoal[0].rate)
        assertEquals(2, m.perGoal[1].done)
        assertEquals(3, m.perGoal[1].scheduled)
    }

    @Test
    fun `perfect days require every scheduled goal done`() {
        val goals = listOf(daily, wednesdays)
        // daily done on 1-10; wednesdays done on the 8th and 15th (missed the 1st).
        val checks = done(daily, *(1..10).toList().toIntArray()) + done(wednesdays, 8, 15)
        val m = monthMetrics(goals, checks, july, july.atDay(15))

        // Days 2-10 are perfect (on non-Wednesdays only the daily goal counts,
        // and Wednesday the 8th had both done). The 1st misses the wednesday
        // goal; the 11th-15th miss the daily goal.
        assertEquals(9, m.perfectDays)
    }

    @Test
    fun `unmarked past occurrences count as not done`() {
        val m = monthMetrics(listOf(daily), emptyList(), july, july.atDay(10))
        assertEquals(10, m.scheduled)
        assertEquals(0, m.done)
        assertEquals(0, m.rate)
        assertEquals(0, m.perfectDays)
    }

    @Test
    fun `no scheduled occurrences yet yields zero rate without dividing`() {
        // asOf Tuesday the 7th precedes... the 1st is a Wednesday, so use a
        // goal scheduled only on Fridays and asOf before the first Friday.
        val fridays = Goal(4, "Salir a correr", july, setOf(DayOfWeek.FRIDAY))
        val m = monthMetrics(listOf(fridays), emptyList(), july, july.atDay(2))
        assertEquals(0, m.scheduled)
        assertEquals(0, m.rate)
    }

    @Test
    fun `archived goals and other months are excluded`() {
        val august = Goal(5, "Meditar", YearMonth.of(2026, 8), DayOfWeek.entries.toSet())
        val m = monthMetrics(
            listOf(daily.copy(archived = true), august),
            emptyList(),
            july,
            july.atDay(15),
        )
        assertEquals(0, m.scheduled)
        assertEquals(emptyList(), m.perGoal.map { it.goal })
    }

    @Test
    fun `asOf past the month end caps at the month`() {
        val checks = done(wednesdays, 1, 8, 15, 22, 29)
        val m = monthMetrics(listOf(wednesdays), checks, july, LocalDate.of(2026, 8, 15))
        assertEquals(5, m.scheduled)
        assertEquals(5, m.done)
        assertEquals(100, m.rate)
    }
}
