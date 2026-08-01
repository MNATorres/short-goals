package com.mnatorres.shortgoals.core

import java.time.LocalDate

/**
 * Consecutive scheduled occurrences of [goal] marked done, counting
 * backwards from the most recent occurrence on or before [asOf].
 *
 * Only scheduled dates participate: rest days can never break a run
 * (a Wednesdays-only streak is a run of consecutive Wednesdays). A past
 * occurrence that is unmarked or marked not-done breaks the run. The
 * occurrence on [asOf] itself is special-cased: while still unmarked it
 * counts as pending — the streak holds at yesterday's value instead of
 * dropping to zero mid-day.
 */
fun currentStreak(goal: Goal, checks: List<DailyCheck>, asOf: LocalDate): Int {
    val done = doneByDate(goal, checks)
    var past = goal.scheduledDates().filter { it <= asOf }.asReversed()
    if (past.firstOrNull() == asOf && asOf !in done) {
        past = past.drop(1)
    }
    var streak = 0
    for (date in past) {
        if (done[date] == true) streak++ else break
    }
    return streak
}

/**
 * The longest run of consecutive scheduled occurrences marked done,
 * over the goal's whole month up to [asOf].
 */
fun bestStreak(goal: Goal, checks: List<DailyCheck>, asOf: LocalDate): Int {
    val done = doneByDate(goal, checks)
    var best = 0
    var run = 0
    for (date in goal.scheduledDates().filter { it <= asOf }) {
        run = if (done[date] == true) run + 1 else 0
        if (run > best) best = run
    }
    return best
}

private fun doneByDate(goal: Goal, checks: List<DailyCheck>): Map<LocalDate, Boolean> =
    checks.filter { it.goalId == goal.id }.associate { it.date to it.done }
