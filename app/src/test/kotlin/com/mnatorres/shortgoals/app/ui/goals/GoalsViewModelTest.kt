package com.mnatorres.shortgoals.app.ui.goals

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.mnatorres.shortgoals.app.data.GoalsRepository
import com.mnatorres.shortgoals.app.data.ShortGoalsDatabase
import java.time.DayOfWeek
import java.time.YearMonth
import kotlin.test.assertEquals
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
class GoalsViewModelTest {

    private lateinit var db: ShortGoalsDatabase
    private lateinit var vm: GoalsViewModel

    private val july = YearMonth.of(2026, 7)
    private val monWedFri = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ShortGoalsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        vm = GoalsViewModel(GoalsRepository(db), july)
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `adding a goal lists it as active with its schedule`() = runTest {
        vm.addGoal("  Ir al gimnasio  ", monWedFri)

        val state = vm.uiState.first { it.active.isNotEmpty() }
        assertEquals("Ir al gimnasio", state.active.single().name)
        assertEquals(monWedFri, state.active.single().weekdays)
        assertTrue(state.archived.isEmpty())
    }

    @Test
    fun `blank names and empty schedules are rejected`() = runTest {
        vm.addGoal("   ", monWedFri)
        vm.addGoal("Leer", emptySet())
        vm.addGoal("Leer 10 páginas", monWedFri)

        val state = vm.uiState.first { it.active.isNotEmpty() }
        assertEquals(listOf("Leer 10 páginas"), state.active.map { it.name })
    }

    @Test
    fun `updating renames and reschedules in place`() = runTest {
        vm.addGoal("Leer", monWedFri)
        val goal = vm.uiState.first { it.active.isNotEmpty() }.active.single()

        vm.updateGoal(goal, "Leer 10 páginas", setOf(DayOfWeek.SUNDAY))

        val updated = vm.uiState.first { it.active.singleOrNull()?.name == "Leer 10 páginas" }
        assertEquals(goal.id, updated.active.single().id)
        assertEquals(setOf(DayOfWeek.SUNDAY), updated.active.single().weekdays)
    }

    @Test
    fun `a fresh month offers to repeat last month's active goals`() = runTest {
        val repo = GoalsRepository(db)
        val june = july.minusMonths(1)
        repo.addGoal("No fumar", june, monWedFri)
        val archivedId = repo.addGoal("Abandonado", june, monWedFri)
        repo.archiveGoal(repo.goals(june).first().single { it.id == archivedId })

        val offered = vm.uiState.first { it.previousMonthGoals.isNotEmpty() }
        assertTrue(offered.showRepeatOffer)
        assertEquals(listOf("No fumar"), offered.previousMonthGoals.map { it.name })

        vm.repeatPreviousMonth()

        val copied = vm.uiState.first { it.active.isNotEmpty() }
        assertEquals("No fumar", copied.active.single().name)
        assertEquals(july, copied.active.single().month)
        assertEquals(monWedFri, copied.active.single().weekdays)
        assertTrue(!copied.showRepeatOffer)
    }

    @Test
    fun `repeat is a no-op once the month has goals`() = runTest {
        val repo = GoalsRepository(db)
        repo.addGoal("Viejo", july.minusMonths(1), monWedFri)
        vm.addGoal("Nuevo", monWedFri)
        vm.uiState.first { it.active.isNotEmpty() && it.previousMonthGoals.isNotEmpty() }

        vm.repeatPreviousMonth()

        val state = vm.uiState.first { it.active.isNotEmpty() }
        assertEquals(listOf("Nuevo"), state.active.map { it.name })
    }

    @Test
    fun `archiving moves the goal out of the active list`() = runTest {
        vm.addGoal("No fumar", monWedFri)
        val goal = vm.uiState.first { it.active.isNotEmpty() }.active.single()

        vm.archive(goal)

        val state = vm.uiState.first { it.archived.isNotEmpty() }
        assertTrue(state.active.isEmpty())
        assertEquals("No fumar", state.archived.single().name)
    }
}
