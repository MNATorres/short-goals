package com.mnatorres.shortgoals.core

import java.time.DayOfWeek
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ModelTest {

    private val july = YearMonth.of(2026, 7)

    @Test
    fun `goal with valid data is created`() {
        val goal = Goal(1, "No fumar", july, DayOfWeek.entries.toSet())
        assertEquals("No fumar", goal.name)
        assertEquals(7, goal.weekdays.size)
    }

    @Test
    fun `goal name must not be blank`() {
        assertFailsWith<IllegalArgumentException> {
            Goal(1, "   ", july, setOf(DayOfWeek.WEDNESDAY))
        }
    }

    @Test
    fun `goal must have at least one weekday`() {
        assertFailsWith<IllegalArgumentException> {
            Goal(1, "Leer 10 páginas", july, emptySet())
        }
    }
}
