package com.mnatorres.shortgoals.app.ui.goals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mnatorres.shortgoals.app.ShortGoalsApp
import com.mnatorres.shortgoals.app.ui.format.monthLabel
import com.mnatorres.shortgoals.app.ui.format.shortLabel
import com.mnatorres.shortgoals.app.ui.theme.DataSmall
import com.mnatorres.shortgoals.app.ui.theme.LabelStyle
import com.mnatorres.shortgoals.app.ui.theme.PanelBorder
import com.mnatorres.shortgoals.app.ui.theme.TextMuted
import com.mnatorres.shortgoals.core.Goal

@Composable
fun GoalsScreen() {
    val app = LocalContext.current.applicationContext as ShortGoalsApp
    val viewModel: GoalsViewModel = viewModel { GoalsViewModel(app.repository) }
    val state by viewModel.uiState.collectAsState()
    GoalsList(state)
}

@Composable
private fun GoalsList(state: GoalsUiState) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Objetivos",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "${state.month.monthLabel()} · ${state.active.size} activos",
            style = DataSmall,
            color = TextMuted,
        )
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.active, key = { it.id }) { goal -> GoalCard(goal) }
            if (state.archived.isNotEmpty()) {
                item {
                    Text(
                        text = "ARCHIVADOS",
                        style = LabelStyle,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                    )
                }
                items(state.archived, key = { it.id }) { goal -> GoalCard(goal, muted = true) }
            }
        }
    }
}

@Composable
private fun GoalCard(goal: Goal, muted: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, PanelBorder),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
        ) {
            Text(
                text = goal.name,
                style = MaterialTheme.typography.bodyMedium,
                color = if (muted) TextMuted else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = goal.weekdays.shortLabel(),
                style = DataSmall,
                color = TextMuted,
            )
        }
    }
}
