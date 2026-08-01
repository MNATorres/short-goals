package com.mnatorres.shortgoals.core

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HeatmapTest {

    private val july = YearMonth.of(2026, 7)
    private val daily = Goal(1, "No fumar", july, DayOfWeek.entries.toSet())
    private val wednesdays = Goal(3, "Ir al gimnasio", july, setOf(DayOfWeek.WEDNESDAY))

    @Test
    fun `level buckets are relative to the day's schedule`() {
        fun cell(done: Int, scheduled: Int) =
            HeatCell(july.atDay(1), scheduled, done, closed = false).level

        assertEquals(HeatLevel.NONE, cell(0, 0))
        assertEquals(HeatLevel.NONE, cell(0, 4))
        assertEquals(HeatLevel.LOW, cell(1, 3))
        assertEquals(HeatLevel.MEDIUM, cell(1, 2))
        assertEquals(HeatLevel.MEDIUM, cell(2, 3))
        assertEquals(HeatLevel.FULL, cell(3, 3))
    }

    @Test
    fun `a light day fully done paints as strongly as a heavy one`() {
        assertEquals(HeatLevel.FULL, HeatCell(july.atDay(5), 3, 3, false).level)
        assertEquals(HeatLevel.FULL, HeatCell(july.atDay(1), 5, 5, false).level)
    }

    @Test
    fun `heatmap covers the month only up to asOf`() {
        val cells = monthHeatmap(listOf(daily), emptyList(), emptyList(), july, july.atDay(5))
        assertEquals(5, cells.size)
        assertEquals(july.atDay(1), cells.first().date)
        assertEquals(july.atDay(5), cells.last().date)
    }

    @Test
    fun `asOf beyond the month covers the whole month`() {
        val cells = monthHeatmap(
            listOf(daily), emptyList(), emptyList(), july, LocalDate.of(2026, 8, 15),
        )
        assertEquals(31, cells.size)
    }

    @Test
    fun `cells count that day's scheduled and done occurrences`() {
        // Wednesday the 1st: both goals scheduled, only the daily one done.
        val checks = listOf(DailyCheck(daily.id, july.atDay(1), true))
        val cells = monthHeatmap(
            listOf(daily, wednesdays), checks, emptyList(), july, july.atDay(2),
        )

        assertEquals(2, cells[0].scheduled)
        assertEquals(1, cells[0].done)
        assertEquals(HeatLevel.MEDIUM, cells[0].level)
        // Thursday the 2nd: only the daily goal, unmarked.
        assertEquals(1, cells[1].scheduled)
        assertEquals(HeatLevel.NONE, cells[1].level)
    }

    @Test
    fun `closed flag mirrors day closes without touching the level`() {
        val closes = listOf(DayClose(july.atDay(1), LocalDateTime.of(2026, 7, 1, 23, 10)))
        val cells = monthHeatmap(listOf(daily), emptyList(), closes, july, july.atDay(2))

        assertTrue(cells[0].closed)
        assertFalse(cells[1].closed)
        assertEquals(HeatLevel.NONE, cells[0].level)
    }
}
