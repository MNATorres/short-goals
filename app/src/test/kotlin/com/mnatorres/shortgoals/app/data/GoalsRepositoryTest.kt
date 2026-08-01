package com.mnatorres.shortgoals.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.YearMonth
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoalsRepositoryTest {

    private lateinit var db: ShortGoalsDatabase
    private lateinit var repo: GoalsRepository

    private val july = YearMonth.of(2026, 7)
    private val monWedFri = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ShortGoalsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = GoalsRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `a goal round-trips with its weekday schedule intact`() = runTest {
        val id = repo.addGoal("Ir al gimnasio", july, monWedFri)

        val goals = repo.goals(july).first()
        assertEquals(1, goals.size)
        assertEquals(id, goals[0].id)
        assertEquals("Ir al gimnasio", goals[0].name)
        assertEquals(monWedFri, goals[0].weekdays)
        assertFalse(goals[0].archived)
    }

    @Test
    fun `goals are windowed by month`() = runTest {
        repo.addGoal("No fumar", july, monWedFri)
        repo.addGoal("Meditar", YearMonth.of(2026, 8), monWedFri)

        assertEquals(listOf("No fumar"), repo.goals(july).first().map { it.name })
    }

    @Test
    fun `archiving keeps the goal but flags it`() = runTest {
        repo.addGoal("No fumar", july, monWedFri)
        repo.archiveGoal(repo.goals(july).first().single())

        val goals = repo.goals(july).first()
        assertEquals(1, goals.size)
        assertTrue(goals[0].archived)
    }

    @Test
    fun `setting a check twice upserts the same mark`() = runTest {
        val id = repo.addGoal("No fumar", july, monWedFri)
        repo.setCheck(id, july.atDay(6), done = true)
        repo.setCheck(id, july.atDay(6), done = false)

        val checks = repo.checks(july).first()
        assertEquals(1, checks.size)
        assertFalse(checks[0].done)
    }

    @Test
    fun `checks are windowed by month`() = runTest {
        val id = repo.addGoal("No fumar", july, monWedFri)
        repo.setCheck(id, july.atDay(31), done = true)
        repo.setCheck(id, july.plusMonths(1).atDay(1), done = true)

        assertEquals(listOf(july.atDay(31)), repo.checks(july).first().map { it.date })
    }

    @Test
    fun `closing and reopening a day round-trips`() = runTest {
        val date = july.atDay(6)
        repo.closeDay(date, LocalDateTime.of(2026, 7, 6, 23, 10))

        val closes = repo.closes(july).first()
        assertEquals(1, closes.size)
        assertEquals(date, closes[0].date)

        repo.reopenDay(date)
        assertTrue(repo.closes(july).first().isEmpty())
    }
}
