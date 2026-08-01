package com.mnatorres.shortgoals.app.ui.today

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mnatorres.shortgoals.app.ShortGoalsApp
import com.mnatorres.shortgoals.app.ui.theme.Amber
import com.mnatorres.shortgoals.app.ui.theme.ControlOutline
import com.mnatorres.shortgoals.app.ui.theme.DataLarge
import com.mnatorres.shortgoals.app.ui.theme.DataSmall
import com.mnatorres.shortgoals.app.ui.theme.LabelStyle
import com.mnatorres.shortgoals.app.ui.theme.OnAmber
import com.mnatorres.shortgoals.app.ui.theme.PanelBorder
import com.mnatorres.shortgoals.app.ui.theme.TextMuted
import com.mnatorres.shortgoals.core.Goal

@Composable
fun TodayScreen() {
    val app = LocalContext.current.applicationContext as ShortGoalsApp
    val viewModel: TodayViewModel = viewModel { TodayViewModel(app.repository) }
    val state by viewModel.uiState.collectAsState()
    TodayContent(state, onToggle = viewModel::toggle)
}

@Composable
private fun TodayContent(state: TodayUiState, onToggle: (TodayItem) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        DayHeader(state)
        Spacer(Modifier.height(16.dp))
        if (!state.hasGoals) {
            EmptyMonth(Modifier.weight(1f))
        } else {
            DayStats(state)
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.items, key = { it.goal.id }) { item ->
                    GoalRow(item, editable = state.close == null, onToggle = { onToggle(item) })
                }
                if (state.resting.isNotEmpty()) {
                    item { RestingCard(state.resting) }
                }
            }
        }
    }
}

@Composable
private fun DayHeader(state: TodayUiState) {
    Text(
        text = state.date.headerLabel(),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun DayStats(state: TodayUiState) {
    val scheduled = state.items.size
    val percent = if (scheduled == 0) 0 else state.doneCount * 100 / scheduled
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, PanelBorder),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(
                text = "${state.doneCount}/$scheduled",
                style = DataLarge,
                color = Amber,
            )
            Text(
                text = "CUMPLIDOS · $percent%",
                style = LabelStyle,
                color = TextMuted,
            )
        }
    }
}

@Composable
private fun GoalRow(item: TodayItem, editable: Boolean, onToggle: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, PanelBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = editable, onClick = onToggle),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            CheckMark(done = item.done)
            Spacer(Modifier.size(10.dp))
            Text(
                text = item.goal.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(text = "×${item.streak}", style = DataSmall, color = TextMuted)
        }
    }
}

@Composable
private fun CheckMark(done: Boolean) {
    val shape = RoundedCornerShape(5.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(22.dp)
            .let { if (done) it.background(Amber, shape) else it.border(1.5.dp, ControlOutline, shape) },
    ) {
        if (done) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Cumplido",
                tint = OnAmber,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun RestingCard(resting: List<Goal>) {
    val detail = resting.joinToString(" · ") { "${it.name} (${it.weekdays.shortLabel()})" }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, ControlOutline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Descansan hoy · $detail",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun EmptyMonth(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Sin objetivos este mes",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Cargalos en la pestaña Objetivos",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
        }
    }
}
