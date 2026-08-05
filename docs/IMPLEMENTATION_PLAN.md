# Short Goals — Phase 1 Implementation Plan (Android app)

Status: **implemented** — milestones M0 through M6 are done and merged;
the app covers this whole document. Phase 2 (the small Ktor sync backend
reusing `:core` in this repo) remains future work. Since the original
plan, CI also gained detekt static analysis, compiler warnings as
errors, a Kover coverage gate on `:core`, and APK artifacts per build.
Design reference: "Tablero" direction (permanent dark mode, amber accent,
month heatmap, monospaced numerals) — approved in the design analysis.

## Scope

Phase 1 delivers a **mobile-only, 100% Kotlin, native Android app** that works
fully offline with a local database. Phase 2 (out of scope here) adds a small
Ktor backend in this same repo for backup/sync, reusing the domain module.

Approved functional spec:

- Goals are created per month, each with a weekday schedule (Mon–Sun selector,
  "Every day" / "Mon–Fri" presets).
- The **Today** screen shows only the goals scheduled for the current date
  (device clock); unscheduled goals appear in a muted "resting today" line.
- Days are **explicitly closed** ("Close the day"). A closed day is read-only
  and shows when it was sealed; it can be reopened for corrections.
- **Unlimited retroactive editing**: arrow navigation between days, plus
  jumping to any day by tapping its heatmap cell.
- **Progress** screen: monthly completion %, month heatmap (intensity relative
  to what was scheduled that day, unsealed days visually marked), per-goal
  completion bars, 14-day sparklines, and streaks.
- Metrics respect schedules: a Wednesdays-only goal is measured only over
  Wednesdays, and its streak counts consecutive scheduled occurrences.
  Unmarked checks on past days count as not done; closing a day does not
  change the numbers, it only records that the day was reviewed.

## Architecture

Two Gradle modules; domain logic stays free of Android so Phase 2's server
can reuse it as-is.

```
short-goals/
├── core/   Pure Kotlin JVM: domain model, schedule logic, streaks, metrics.
│           No Android dependencies. Exhaustively unit-tested.
└── app/    Android application: Jetpack Compose UI, Room persistence,
            ViewModels. Depends on :core.
```

### Stack

| Concern       | Choice                                          |
|---------------|--------------------------------------------------|
| Language      | Kotlin 2.0.x (already in repo)                   |
| UI            | Jetpack Compose + Material 3, single dark theme  |
| Persistence   | Room (SQLite) with KSP                           |
| Async         | Coroutines + Flow (Room exposes Flows)           |
| Navigation    | Navigation Compose (3 tabs + day navigation)     |
| Dates         | `java.time` (`LocalDate`), minSdk 26             |
| DI            | Manual (app is small; no framework needed)       |
| Tests         | kotlin.test / JUnit in :core, Room + ViewModel tests in :app |

### Data model

Three tables; every metric is derived, nothing denormalized.

| Entity       | Fields                                   | Purpose                          |
|--------------|-------------------------------------------|----------------------------------|
| `Goal`       | id, name, month, weekdays (set of Mon–Sun) | The month's goals                |
| `DailyCheck` | goalId, date, done                        | One mark per goal per scheduled day |
| `DayClose`   | date, closedAt                            | Which days were sealed, and when |

## Milestones

Each milestone ends in a working, committed state. Commits within a milestone
are atomic (see conventions below).

### M0 — Project scaffolding

Convert the single-module JVM skeleton into the multi-module Android build.

- `Add Gradle version catalog for plugin and dependency versions`
- `Restructure build into :core and :app modules`
- `Set up Android app module with Jetpack Compose and Material 3`
- `Add dark Tablero theme with amber accent and tabular numerals`
- `Add GitHub Actions workflow running unit tests on push`
- `Update CLAUDE.md with module layout and Android commands`

Exit criteria: `gradlew :core:test` green, `:app` installs and shows a themed
empty shell with the three-tab navigation.

### M1 — Domain logic in :core (the hard part, fully tested)

Pure functions over the three entities. Every commit pairs code with its tests.

- `Add Goal, DailyCheck and DayClose domain models`
- `Add schedule resolution for which goals apply on a date`
- `Add streak calculation over consecutive scheduled occurrences`
- `Add monthly metrics: completion rate, perfect days, per-goal totals`
- `Add heatmap level computation relative to scheduled goals per day`

Exit criteria: metrics engine handles the tricky cases — Wednesdays-only goals,
months with 4 vs 5 occurrences of a weekday, streaks across rest days, days
with nothing scheduled.

### M2 — Persistence

- `Add Room database with goal, daily_check and day_close tables`
- `Add repository exposing goals, checks and day state as Flows`
- `Add repository tests against an in-memory database`

### M3 — Today screen (the daily ritual)

- `Add Today screen listing goals scheduled for the current date`
- `Add check toggling with per-goal streak counters`
- `Add resting-today section for goals not scheduled`
- `Add explicit day close flow with sealed read-only state`
- `Add day navigation arrows for retroactive editing`
- `Add reopen flow for sealed days`

### M4 — Goals screen

- `Add Goals screen listing the current month's goals`
- `Add goal creation form with weekday selector and presets`
- `Add goal editing`
- `Add goal archiving instead of deletion to preserve history`

### M5 — Progress screen

- `Add Progress screen with monthly completion header`
- `Add month heatmap with unsealed-day markers`
- `Add per-goal stat rows with completion bars and streaks`
- `Add 14-day sparklines per goal`
- `Add navigation from heatmap cell to that day's detail`

### M6 — Polish and release

- `Add empty states for first run and months without goals`
- `Add month rollover prompt offering to copy last month's goals` (see open
  question below)
- `Add app icon and launch theme`
- `Add release build configuration` (distribution: direct APK install —
  personal app, no Play Store needed)

## Commit conventions

- **Atomic**: one logical change per commit. The project compiles and all
  tests pass at every commit.
- **English**, imperative mood, subject ≤ 72 chars: `Add streak calculation
  over consecutive scheduled occurrences` — not "added", "fixes", or Spanish.
- The subject says *what*; the body (when the change isn't self-evident)
  says *why* and records any non-obvious decision.
- Tests land in the same commit as the code they cover, never separately.
- No mixed commits (e.g. refactor + feature); mechanical refactors are their
  own commit stating they are behavior-preserving.

## Testing strategy

- `:core` carries the real complexity (schedules, streaks, metrics) and gets
  exhaustive unit tests — it is pure Kotlin, so tests are fast JVM tests.
- `:app` gets repository tests (in-memory Room) and ViewModel tests; UI is
  kept thin so Compose UI tests stay minimal.
- CI runs the full test suite on every push.

## Prerequisites & risks

- **Developer machine**: Android Studio + Android SDK, and a device or
  emulator (Windows is fine). The repo currently has no Android tooling;
  M0 introduces it.
- **Assumption**: the phone is Android. An iOS target would require Compose
  Multiplatform and a macOS build machine — revisit the plan if so.
- **Open question (decide before M6)**: on the 1st of a new month the goal
  list starts empty. Proposal: prompt once — "Repeat July's goals?" — and
  copy on confirm.
