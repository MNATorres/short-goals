package com.mnatorres.shortgoals.core

import java.time.LocalDate
import java.time.YearMonth

/** True when this goal must appear on [date]'s checklist. */
fun Goal.isScheduledOn(date: LocalDate): Boolean =
    !archived && YearMonth.from(date) == month && date.dayOfWeek in weekdays

/** Goals to show as [date]'s checklist, in the list's original order. */
fun List<Goal>.scheduledOn(date: LocalDate): List<Goal> =
    filter { it.isScheduledOn(date) }

/** Goals active in [date]'s month but resting that weekday ("Descansan hoy"). */
fun List<Goal>.restingOn(date: LocalDate): List<Goal> =
    filter { !it.archived && it.month == YearMonth.from(date) && date.dayOfWeek !in it.weekdays }

/** Every date in the goal's month on which it is scheduled, in order. */
fun Goal.scheduledDates(): List<LocalDate> =
    if (archived) emptyList()
    else (1..month.lengthOfMonth()).map(month::atDay).filter { it.dayOfWeek in weekdays }
