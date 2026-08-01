package com.mnatorres.shortgoals.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [GoalEntity::class, DailyCheckEntity::class, DayCloseEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class ShortGoalsDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun dailyCheckDao(): DailyCheckDao
    abstract fun dayCloseDao(): DayCloseDao

    companion object {
        fun build(context: Context): ShortGoalsDatabase =
            Room.databaseBuilder(context, ShortGoalsDatabase::class.java, "shortgoals.db")
                .build()
    }
}
