package com.mnatorres.shortgoals.app.ui.format

import java.time.DayOfWeek

private val INITIALS = mapOf(
    DayOfWeek.MONDAY to "L",
    DayOfWeek.TUESDAY to "M",
    DayOfWeek.WEDNESDAY to "X",
    DayOfWeek.THURSDAY to "J",
    DayOfWeek.FRIDAY to "V",
    DayOfWeek.SATURDAY to "S",
    DayOfWeek.SUNDAY to "D",
)

private val MON_TO_FRI = setOf(
    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
)

/** "Todos los días", "L–V", or initials like "L X V". */
fun Set<DayOfWeek>.shortLabel(): String = when {
    size == DayOfWeek.entries.size -> "Todos los días"
    this == MON_TO_FRI -> "L–V"
    else -> sortedBy { it.value }.joinToString(" ") { INITIALS.getValue(it) }
}
