package com.mnatorres.shortgoals.app.ui.today

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.mnatorres.shortgoals.app.data.GoalsRepository
import com.mnatorres.shortgoals.app.data.ShortGoalsDatabase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
class TodayViewModelTest {

    private lateinit var db: ShortGoalsDatabase
    private lateinit var repo: GoalsRepository

    private val july = YearMonth.of(2026, 7)

    // 2026-07-29 is a Wednesday.
    private val today = july.atDay(29)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ShortGoalsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = GoalsRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    private fun viewModel() = TodayViewModel(repo) { today }

    private suspend fun seedGoals() {
        repo.addGoal("No fumar", july, DayOfWeek.entries.toSet())
        repo.addGoal("Ir al gimnasio", july, setOf(DayOfWeek.WEDNESDAY))
        repo.addGoal("Salir a caminar", july, setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
    }

    @Test
    fun `wednesday lists scheduled goals and rests the weekend one`() = runTest {
        seedGoals()
        val state = viewModel().uiState.first { it.hasGoals }

        assertEquals(listOf("No fumar", "Ir al gimnasio"), state.items.map { it.goal.name })
        assertEquals(listOf("Salir a caminar"), state.resting.map { it.name })
        assertTrue(state.isToday)
        assertNull(state.close)
    }

    @Test
    fun `toggling marks and unmarks a goal`() = runTest {
        seedGoals()
        val vm = viewModel()

        val initial = vm.uiState.first { it.hasGoals }
        vm.toggle(initial.items.first())
        val marked = vm.uiState.first { it.doneCount == 1 }
        assertTrue(marked.items.first().done)
        assertEquals(1, marked.items.first().streak)

        vm.toggle(marked.items.first())
        val unmarked = vm.uiState.first { it.doneCount == 0 }
        assertFalse(unmarked.items.first().done)
    }

    @Test
    fun `a closed day rejects toggles until reopened`() = runTest {
        seedGoals()
        val vm = viewModel()

        val open = vm.uiState.first { it.hasGoals }
        vm.closeDay()
        val closed = vm.uiState.first { it.close != null }
        assertNotNull(closed.close)

        vm.toggle(closed.items.first())
        assertEquals(0, vm.uiState.first { it.close != null }.doneCount)

        vm.reopenDay()
        val reopened = vm.uiState.first { it.close == null }
        assertNull(reopened.close)
        assertEquals(open.items.map { it.goal.id }, reopened.items.map { it.goal.id })
    }

    @Test
    fun `navigation reaches any past day but never the future`() = runTest {
        seedGoals()
        val vm = viewModel()
        vm.uiState.first { it.hasGoals }

        vm.nextDay()
        assertEquals(today, vm.uiState.first { it.hasGoals }.date)

        vm.previousDay()
        val yesterday = vm.uiState.first { it.date != today }
        assertEquals(today.minusDays(1), yesterday.date)
        assertFalse(yesterday.isToday)

        vm.nextDay()
        assertEquals(today, vm.uiState.first { it.date == today }.date)
    }

    @Test
    fun `goTo jumps to a past day and clamps the future to today`() = runTest {
        seedGoals()
        val vm = viewModel()
        vm.uiState.first { it.hasGoals }

        vm.goTo(july.atDay(4))
        assertEquals(july.atDay(4), vm.uiState.first { it.date != today }.date)

        vm.goTo(july.atDay(31))
        assertEquals(today, vm.uiState.first { it.date == today }.date)
    }

    @Test
    fun `a month without goals reports the empty state`() = runTest {
        val state = viewModel().uiState.first { it.date == today }
        assertFalse(state.hasGoals)
        assertTrue(state.items.isEmpty())
    }
}
