package com.mnatorres.shortgoals.app.ui.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.mnatorres.shortgoals.app.ui.theme.Amber
import com.mnatorres.shortgoals.app.ui.theme.DataLarge
import com.mnatorres.shortgoals.app.ui.theme.DataSmall
import com.mnatorres.shortgoals.app.ui.theme.LabelStyle
import com.mnatorres.shortgoals.app.ui.theme.PanelBorder
import com.mnatorres.shortgoals.app.ui.theme.TextMuted

@Composable
fun ProgressScreen() {
    val app = LocalContext.current.applicationContext as ShortGoalsApp
    val viewModel: ProgressViewModel = viewModel { ProgressViewModel(app.repository) }
    val state by viewModel.uiState.collectAsState()
    ProgressContent(state)
}

@Composable
private fun ProgressContent(state: ProgressUiState) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Progreso",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(text = state.month.monthLabel(), style = DataSmall, color = TextMuted)
        Spacer(Modifier.height(16.dp))
        if (!state.hasGoals) {
            EmptyProgress(Modifier.weight(1f))
        } else {
            MonthPanels(state)
        }
    }
}

@Composable
private fun MonthPanels(state: ProgressUiState) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatPanel(
            value = "${state.rate}%",
            label = "CUMPLIMIENTO · ${state.done} DE ${state.scheduled}",
            modifier = Modifier.weight(1f),
        )
        StatPanel(
            value = "${state.perfectDays}",
            label = "DÍAS PERFECTOS",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun StatPanel(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, PanelBorder),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(text = value, style = DataLarge, color = Amber)
            Text(text = label, style = LabelStyle, color = TextMuted)
        }
    }
}

@Composable
private fun EmptyProgress(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Todavía no hay nada que medir",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Cargá objetivos y marcá tus primeros días",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
        }
    }
}
