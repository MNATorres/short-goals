package com.mnatorres.shortgoals.app.ui.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.mnatorres.shortgoals.app.ui.theme.Amber
import com.mnatorres.shortgoals.app.ui.theme.ControlOutline
import com.mnatorres.shortgoals.app.ui.theme.DataSmall
import com.mnatorres.shortgoals.app.ui.theme.TextMuted
import java.time.LocalDate

@Composable
fun TodayScreen() {
    val app = LocalContext.current.applicationContext as ShortGoalsApp
    val viewModel: TodayViewModel = viewModel { TodayViewModel(app.repository) }
    val state by viewModel.uiState.collectAsState()
    TodayContent(
        state = state,
        onToggle = viewModel::toggle,
        onClose = viewModel::closeDay,
        onReopen = viewModel::reopenDay,
        onPrevious = viewModel::previousDay,
        onNext = viewModel::nextDay,
    )
}

@Composable
private fun TodayContent(
    state: TodayUiState,
    onToggle: (TodayItem) -> Unit,
    onClose: () -> Unit,
    onReopen: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        DayHeader(state, onPrevious, onNext)
        Spacer(Modifier.height(16.dp))
        if (!state.hasGoals) {
            EmptyMonth(Modifier.weight(1f))
        } else {
            DayBody(state, onToggle)
            Spacer(Modifier.height(12.dp))
            if (state.close == null) {
                CloseDayButton(state, onClose)
            } else {
                ReopenButton(onReopen)
            }
        }
    }
}

@Composable
private fun ColumnScope.DayBody(state: TodayUiState, onToggle: (TodayItem) -> Unit) {
    SealBanner(state)
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

@Composable
private fun DayHeader(state: TodayUiState, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Día anterior",
                tint = Amber,
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = state.date.headerLabel(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = state.date.relativeLabel(LocalDate.now()),
                style = DataSmall,
                color = TextMuted,
            )
        }
        IconButton(onClick = onNext, enabled = !state.isToday) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Día siguiente",
                tint = if (state.isToday) ControlOutline else Amber,
            )
        }
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
