package com.mnatorres.shortgoals.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mnatorres.shortgoals.app.ui.goals.GoalsScreen
import com.mnatorres.shortgoals.app.ui.progress.ProgressScreen
import com.mnatorres.shortgoals.app.ui.theme.TextMuted
import com.mnatorres.shortgoals.app.ui.today.TodayScreen
import java.time.LocalDate

@Composable
fun AppShell() {
    var selected by rememberSaveable { mutableStateOf(Tab.Today) }
    var requestedDate by remember { mutableStateOf<LocalDate?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selected == tab,
                        onClick = { selected = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.background,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selected) {
                Tab.Today -> TodayScreen(
                    requestedDate = requestedDate,
                    onRequestConsumed = { requestedDate = null },
                )
                Tab.Goals -> GoalsScreen()
                Tab.Progress -> ProgressScreen(
                    onOpenDay = { date ->
                        requestedDate = date
                        selected = Tab.Today
                    },
                )
            }
        }
    }
}

