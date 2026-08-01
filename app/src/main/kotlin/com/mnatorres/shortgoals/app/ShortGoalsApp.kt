package com.mnatorres.shortgoals.app

import android.app.Application
import com.mnatorres.shortgoals.app.data.GoalsRepository
import com.mnatorres.shortgoals.app.data.ShortGoalsDatabase

class ShortGoalsApp : Application() {

    /** App-wide singletons; the app is small enough to not need a DI framework. */
    val repository: GoalsRepository by lazy {
        GoalsRepository(ShortGoalsDatabase.build(this))
    }
}
