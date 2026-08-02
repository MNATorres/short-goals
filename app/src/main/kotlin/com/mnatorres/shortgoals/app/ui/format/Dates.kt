package com.mnatorres.shortgoals.app.ui.format

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private val SPANISH: Locale = Locale.forLanguageTag("es")
private val DAY_OF_MONTH = DateTimeFormatter.ofPattern("d 'de' MMMM", SPANISH)
private val CLOSED_AT = DateTimeFormatter.ofPattern("'el' dd/MM 'a las' HH:mm", SPANISH)

/** "Miércoles 29 de julio" */
fun LocalDate.headerLabel(): String {
    val weekday = dayOfWeek.getDisplayName(TextStyle.FULL, SPANISH)
        .replaceFirstChar { it.titlecase(SPANISH) }
    return "$weekday ${format(DAY_OF_MONTH)}"
}

/** "Hoy", "Ayer", "Hace 12 días" */
fun LocalDate.relativeLabel(today: LocalDate): String =
    when (val days = ChronoUnit.DAYS.between(this, today)) {
        0L -> "Hoy"
        1L -> "Ayer"
        else -> "Hace $days días"
    }

/** "el 22/07 a las 23:10" */
fun LocalDateTime.closedAtLabel(): String = format(CLOSED_AT)
