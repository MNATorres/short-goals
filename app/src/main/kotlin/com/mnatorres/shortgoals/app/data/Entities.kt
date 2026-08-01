package com.mnatorres.shortgoals.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

@Entity(tableName = "goal")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val month: YearMonth,
    val weekdays: Set<DayOfWeek>,
    val archived: Boolean = false,
)

@Entity(
    tableName = "daily_check",
    primaryKeys = ["goalId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class DailyCheckEntity(
    val goalId: Long,
    val date: LocalDate,
    val done: Boolean,
)

@Entity(tableName = "day_close")
data class DayCloseEntity(
    @PrimaryKey val date: LocalDate,
    val closedAt: LocalDateTime,
)
