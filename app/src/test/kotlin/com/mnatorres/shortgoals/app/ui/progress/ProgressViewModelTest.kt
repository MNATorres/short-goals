package com.mnatorres.shortgoals.app.ui.progress

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.mnatorres.shortgoals.app.data.GoalsRepository
import com.mnatorres.shortgoals.app.data.ShortGoalsDatabase
import com.mnatorres.shortgoals.core.HeatLevel
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.YearMonth
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ProgressViewModelTest {

    private lateinit var db: ShortGoalsDatabase
    private lateinit var repo: GoalsRepository
    private lateinit var vm: ProgressViewModel

    private val july = YearMonth.of(2026, 7)

    // 2026-07-29 is a Wednesday; July's Wednesdays are the 1st, 8th, 15th, 22nd and 29th.
    private val today = july.atDay(29)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ShortGoalsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = GoalsRepository(db)
        vm = ProgressViewModel(repo) { today }
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    private suspend fun seed() {
        val daily = repo.addGoal("No fumar", july, DayOfWeek.entries.toSet())
        val wednesdays = repo.addGoal("Ir al gimnasio", july, setOf(DayOfWeek.WEDNESDAY))
        // Daily goal done the 27th, 28th and 29th; Wednesdays done except today's.
        listOf(27, 28, 29).forEach { repo.setCheck(daily, july.atDay(it), done = true) }
        listOf(1, 8, 15, 22).forEach { repo.setCheck(wednesdays, july.atDay(it), done = true) }
        repo.closeDay(july.atDay(28), LocalDateTime.of(2026, 7, 28, 23, 0))
    }

    @Test
    fun `month header aggregates only what was scheduled so far`() = runTest {
        seed()
        val state = vm.uiState.first { it.hasGoals }

        // Daily: 3 of 29. Wednesdays: 4 of 5. Total 7 of 34.
        assertEquals(34, state.scheduled)
        assertEquals(7, state.done)
        assertEquals(7 * 100 / 34, state.rate)
        // Only the 27th and 28th had every scheduled goal done.
        assertEquals(2, state.perfectDays)
    }

    @Test
    fun `heatmap covers the month to date with seal markers`() = runTest {
        seed()
        val state = vm.uiState.first { it.hasGoals }

        assertEquals(29, state.heatmap.size)
        val day28 = state.heatmap.single { it.date == july.atDay(28) }
        assertTrue(day28.closed)
        assertEquals(HeatLevel.FULL, day28.level)
        val day29 = state.heatmap.single { it.date == today }
        assertFalse(day29.closed)
        assertEquals(HeatLevel.MEDIUM, day29.level) // 1 of 2 scheduled done
    }

    @Test
    fun `per-goal stats respect each goal's schedule`() = runTest {
        seed()
        val state = vm.uiState.first { it.hasGoals }

        val wednesdays = state.goals.single { it.goal.name == "Ir al gimnasio" }
        assertEquals(4, wednesdays.done)
        assertEquals(5, wednesdays.scheduled)
        assertEquals(80, wednesdays.rate)
        // Today's occurrence is pending, so the streak holds at 4.
        assertEquals(4, wednesdays.streak)
        // Sparkline: the month's five Wednesdays, today's still unmarked.
        assertEquals(listOf(true, true, true, true, false), wednesdays.spark)
    }
}
