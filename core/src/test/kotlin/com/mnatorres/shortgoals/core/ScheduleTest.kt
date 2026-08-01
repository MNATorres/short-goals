package com.mnatorres.shortgoals.core

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScheduleTest {

    private val july = YearMonth.of(2026, 7)
    private val everyDay = DayOfWeek.entries.toSet()
    private val monToFri = setOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
    )

    private val daily = Goal(1, "No fumar", july, everyDay)
    private val weekdaysOnly = Goal(2, "Practicar inglés", july, monToFri)
    private val wednesdays = Goal(3, "Ir al gimnasio", july, setOf(DayOfWeek.WEDNESDAY))

    // July 2026: the 1st is a Wednesday, the 5th a Sunday, the 28th a Tuesday.

    @Test
    fun `goal is scheduled on a selected weekday inside its month`() {
        assertTrue(wednesdays.isScheduledOn(LocalDate.of(2026, 7, 1)))
    }

    @Test
    fun `goal is not scheduled on an unselected weekday`() {
        assertFalse(wednesdays.isScheduledOn(LocalDate.of(2026, 7, 28)))
    }

    @Test
    fun `goal is not scheduled outside its month even on a selected weekday`() {
        // 2026-08-05 is a Wednesday, but the goal belongs to July.
        assertFalse(wednesdays.isScheduledOn(LocalDate.of(2026, 8, 5)))
    }

    @Test
    fun `archived goal is never scheduled`() {
        val archived = wednesdays.copy(archived = true)
        assertFalse(archived.isScheduledOn(LocalDate.of(2026, 7, 1)))
        assertEquals(emptyList(), archived.scheduledDates())
    }

    @Test
    fun `checklist for a wednesday includes all three goals`() {
        val goals = listOf(daily, weekdaysOnly, wednesdays)
        assertEquals(goals, goals.scheduledOn(LocalDate.of(2026, 7, 15)))
    }

    @Test
    fun `checklist for a sunday includes only the daily goal`() {
        val goals = listOf(daily, weekdaysOnly, wednesdays)
        val sunday = LocalDate.of(2026, 7, 5)
        assertEquals(listOf(daily), goals.scheduledOn(sunday))
        assertEquals(listOf(weekdaysOnly, wednesdays), goals.restingOn(sunday))
    }

    @Test
    fun `resting excludes archived goals and other months`() {
        val goals = listOf(daily, wednesdays.copy(archived = true))
        val sunday = LocalDate.of(2026, 7, 5)
        assertEquals(emptyList(), goals.restingOn(sunday))
        assertEquals(emptyList(), goals.restingOn(LocalDate.of(2026, 8, 2)))
    }

    @Test
    fun `wednesdays-only goal has five occurrences in july 2026`() {
        val expected = listOf(1, 8, 15, 22, 29).map { july.atDay(it) }
        assertEquals(expected, wednesdays.scheduledDates())
    }

    @Test
    fun `mon-fri goal has 23 occurrences in july 2026`() {
        assertEquals(23, weekdaysOnly.scheduledDates().size)
    }

    @Test
    fun `daily goal covers every day of the month`() {
        assertEquals(31, daily.scheduledDates().size)
    }
}
