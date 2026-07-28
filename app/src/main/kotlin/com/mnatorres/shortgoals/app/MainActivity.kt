package com.mnatorres.shortgoals.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mnatorres.shortgoals.app.ui.AppShell
import com.mnatorres.shortgoals.app.ui.theme.ShortGoalsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShortGoalsTheme {
                AppShell()
            }
        }
    }
}
