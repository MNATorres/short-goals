# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Short Goals — a personal daily-goals tracker, built as a native Android app in 100% Kotlin. Monthly goals with weekday schedules, an explicit end-of-day close, unlimited retroactive editing, and progress metrics (completion %, streaks, month heatmap). Design: permanent dark "Tablero" theme with amber accent. See `docs/IMPLEMENTATION_PLAN.md` for the full spec, milestones and commit conventions.

## Modules

- `:core` — pure Kotlin JVM. Domain model and all metric logic (schedules, streaks, completion). No Android dependencies; a future Ktor server reuses it as-is. This module carries the exhaustive unit tests.
- `:app` — the Android application: Jetpack Compose + Material 3 UI, Room persistence. Package `com.mnatorres.shortgoals.app`, sources under `app/src/main/kotlin/`.

Dependency versions live in `gradle/libs.versions.toml` (version catalog).

## Commands

The primary dev environment is Windows (PowerShell) with Android Studio. Use `gradlew.bat` from PowerShell/cmd; the `./gradlew` wrapper works from the Bash tool / Unix shells.

```powershell
gradlew.bat build              # compile everything + run all tests + assemble APKs
gradlew.bat :core:test         # run the domain unit tests only (fast, no Android SDK needed)
gradlew.bat :app:assembleDebug # build the debug APK
gradlew.bat :app:installDebug  # install on a connected device/emulator
```

Run a single test class or method (kotlin.test names can contain spaces via backticks):

```powershell
gradlew.bat :core:test --tests "com.mnatorres.shortgoals.SetupTest"
```

## Environment notes

- Building `:app` requires the Android SDK (`ANDROID_HOME` or `local.properties`). In sandboxed environments without it (e.g. Claude Code on the web, where `dl.google.com` may be blocked), only `:core` tasks run locally — push and let CI build the app: `.github/workflows/ci.yml` runs `./gradlew build` on every push.
- Requires JDK 21+ (`jvmToolchain(21)` in `:core`, `jvmTarget 21` in `:app`).

## Stack & conventions

- Kotlin 2.0.21; Compose via the Kotlin Compose compiler plugin and the Compose BOM; Material 3; Room (KSP) for persistence; `java.time` for dates (minSdk 26 — no desugaring).
- Tests use `kotlin.test` on the JUnit Platform (`useJUnitPlatform()`).
- Commits: atomic (one logical change, project compiles and tests pass at every commit), English, imperative subject ≤ 72 chars, body explains the why; tests land in the same commit as the code they cover. Full conventions in `docs/IMPLEMENTATION_PLAN.md`.
- UI copy is Spanish (the app's user language); code, comments and commits are English.
- There is no lint/formatter configured yet.
