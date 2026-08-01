package com.mnatorres.shortgoals.app.data

import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

/** ISO-8601 strings keep the database readable and sort naturally. */
class Converters {

    @TypeConverter
    fun fromLocalDate(value: LocalDate): String = value.toString()

    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)

    @TypeConverter
    fun fromYearMonth(value: YearMonth): String = value.toString()

    @TypeConverter
    fun toYearMonth(value: String): YearMonth = YearMonth.parse(value)

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): String = value.toString()

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime = LocalDateTime.parse(value)

    /** ISO weekday numbers, sorted, comma-separated: "1,3,5" = Mon, Wed, Fri. */
    @TypeConverter
    fun fromWeekdays(value: Set<DayOfWeek>): String =
        value.sortedBy { it.value }.joinToString(",") { it.value.toString() }

    @TypeConverter
    fun toWeekdays(value: String): Set<DayOfWeek> =
        if (value.isEmpty()) emptySet()
        else value.split(",").mapTo(mutableSetOf()) { DayOfWeek.of(it.toInt()) }
}
