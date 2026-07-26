# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Short Goals — a personal short-term goals tracker in Kotlin (JVM). Currently a project skeleton only: `Main.kt` just prints a banner and there is no domain logic yet.

## Commands

The primary dev environment is Windows (PowerShell). Use `gradlew.bat` from PowerShell/cmd; the `./gradlew` wrapper works from the Bash tool / Unix shells.

```powershell
gradlew.bat build        # compile + run all tests
gradlew.bat run          # run the app (main class com.mnatorres.shortgoals.MainKt)
gradlew.bat test         # run tests only
gradlew.bat clean        # remove build/ outputs
```

Run a single test class or method (kotlin.test names can contain spaces via backticks):

```powershell
gradlew.bat test --tests "com.mnatorres.shortgoals.MainTest"
gradlew.bat test --tests "com.mnatorres.shortgoals.MainTest.test setup runs"
```

## Stack & conventions

- Kotlin 2.0.21 on the JVM 21 toolchain (`jvmToolchain(21)` in `build.gradle.kts`) — requires JDK 21+.
- Tests use `kotlin.test` running on the JUnit Platform (`useJUnitPlatform()`).
- All code lives under the `com.mnatorres.shortgoals` package: app code in `src/main/kotlin/`, tests mirroring it in `src/test/kotlin/`.
- There is no lint/formatter configured yet.
