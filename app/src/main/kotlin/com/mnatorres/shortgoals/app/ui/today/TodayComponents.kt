package com.mnatorres.shortgoals.app.ui.today

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mnatorres.shortgoals.app.ui.format.closedAtLabel
import com.mnatorres.shortgoals.app.ui.format.shortLabel
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
internal fun DayStats(state: TodayUiState) {
    val scheduled = state.items.size
    val percent = if (scheduled == 0) 0 else state.doneCount * 100 / scheduled
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, PanelBorder),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(text = "${state.doneCount}/$scheduled", style = DataLarge, color = Amber)
            Text(text = "CUMPLIDOS · $percent%", style = LabelStyle, color = TextMuted)
        }
    }
}

@Composable
internal fun GoalRow(item: TodayItem, editable: Boolean, onToggle: () -> Unit) {
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
internal fun RestingCard(resting: List<Goal>) {
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
internal fun SealBanner(state: TodayUiState) {
    val close = state.close
    when {
        close != null -> Banner(
            tag = "CERRADO",
            tagColor = Amber,
            text = "Cerraste este día ${close.closedAt.closedAtLabel()}",
        )
        !state.isToday -> Banner(
            tag = "SIN CERRAR",
            tagColor = TextMuted,
            text = "Marcá lo que cumpliste y cerralo cuando quieras",
        )
        else -> return
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun Banner(tag: String, tagColor: Color, text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, PanelBorder),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Text(
                text = tag,
                style = DataSmall,
                color = tagColor,
                modifier = Modifier
                    .border(1.dp, tagColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
            Spacer(Modifier.size(10.dp))
            Text(text = text, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
    }
}

@Composable
internal fun CloseDayButton(state: TodayUiState, onClose: () -> Unit) {
    Button(
        onClick = onClose,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = OnAmber),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(if (state.isToday) "Cerrar el día" else "Cerrar este día")
    }
}

@Composable
internal fun ReopenButton(onReopen: () -> Unit) {
    OutlinedButton(
        onClick = onReopen,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Reabrir para editar", color = MaterialTheme.colorScheme.onSurface)
    }
}
