package com.mnatorres.shortgoals.app.data

import com.mnatorres.shortgoals.core.DailyCheck
import com.mnatorres.shortgoals.core.DayClose
import com.mnatorres.shortgoals.core.Goal

fun GoalEntity.toDomain() = Goal(id, name, month, weekdays, archived)

fun Goal.toEntity() = GoalEntity(id, name, month, weekdays, archived)

fun DailyCheckEntity.toDomain() = DailyCheck(goalId, date, done)

fun DayCloseEntity.toDomain() = DayClose(date, closedAt)
