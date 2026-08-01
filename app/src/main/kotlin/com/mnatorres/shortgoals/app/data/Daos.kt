package com.mnatorres.shortgoals.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    /** All of the month's goals, archived included — callers filter. */
    @Query("SELECT * FROM goal WHERE month = :month ORDER BY id")
    fun forMonth(month: YearMonth): Flow<List<GoalEntity>>

    @Insert
    suspend fun insert(goal: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)
}

@Dao
interface DailyCheckDao {

    @Query("SELECT * FROM daily_check WHERE date BETWEEN :from AND :to")
    fun between(from: LocalDate, to: LocalDate): Flow<List<DailyCheckEntity>>

    @Upsert
    suspend fun upsert(check: DailyCheckEntity)
}

@Dao
interface DayCloseDao {

    @Query("SELECT * FROM day_close WHERE date BETWEEN :from AND :to")
    fun between(from: LocalDate, to: LocalDate): Flow<List<DayCloseEntity>>

    @Upsert
    suspend fun upsert(close: DayCloseEntity)

    @Query("DELETE FROM day_close WHERE date = :date")
    suspend fun delete(date: LocalDate)
}
