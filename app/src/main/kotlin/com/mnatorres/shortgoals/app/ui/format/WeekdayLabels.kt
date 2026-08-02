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

/** The two schedule presets offered by the goal editor. */
val EVERY_DAY: Set<DayOfWeek> = DayOfWeek.entries.toSet()
val MON_TO_FRI: Set<DayOfWeek> = setOf(
    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
)

/** Single-letter Spanish initial: L M X J V S D. */
fun DayOfWeek.initial(): String = INITIALS.getValue(this)

/** "Todos los días", "L–V", or initials like "L X V". */
fun Set<DayOfWeek>.shortLabel(): String = when {
    this == EVERY_DAY -> "Todos los días"
    this == MON_TO_FRI -> "L–V"
    else -> sortedBy { it.value }.joinToString(" ") { INITIALS.getValue(it) }
}
