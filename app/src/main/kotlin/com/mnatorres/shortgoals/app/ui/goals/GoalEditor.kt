package com.mnatorres.shortgoals.app.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mnatorres.shortgoals.app.ui.format.EVERY_DAY
import com.mnatorres.shortgoals.app.ui.format.MON_TO_FRI
import com.mnatorres.shortgoals.app.ui.format.initial
import com.mnatorres.shortgoals.app.ui.theme.Amber
import com.mnatorres.shortgoals.app.ui.theme.ControlOutline
import com.mnatorres.shortgoals.app.ui.theme.DataSmall
import com.mnatorres.shortgoals.app.ui.theme.LabelStyle
import com.mnatorres.shortgoals.app.ui.theme.OnAmber
import com.mnatorres.shortgoals.app.ui.theme.TextMuted
import com.mnatorres.shortgoals.core.Goal
import java.time.DayOfWeek

@Composable
internal fun GoalEditor(
    goal: Goal?,
    onSave: (String, Set<DayOfWeek>) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(goal?.name.orEmpty()) }
    var weekdays by remember { mutableStateOf(goal?.weekdays ?: EVERY_DAY) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = if (goal == null) "Nuevo objetivo" else "Editar objetivo",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(16.dp))
        Text(text = "NOMBRE", style = LabelStyle, color = TextMuted)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            placeholder = { Text("Leer 10 páginas", color = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Text(text = "¿QUÉ DÍAS?", style = LabelStyle, color = TextMuted)
        Spacer(Modifier.height(8.dp))
        WeekdayPicker(weekdays) { weekdays = it }
        Spacer(Modifier.height(10.dp))
        PresetRow(weekdays) { weekdays = it }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { onSave(name, weekdays) },
            enabled = name.isNotBlank() && weekdays.isNotEmpty(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = OnAmber),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Guardar objetivo")
        }
        Spacer(Modifier.height(4.dp))
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar", color = TextMuted)
        }
    }
}

@Composable
private fun WeekdayPicker(selected: Set<DayOfWeek>, onChange: (Set<DayOfWeek>) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        DayOfWeek.entries.forEach { day ->
            val isSelected = day in selected
            DayChip(
                label = day.initial(),
                isSelected = isSelected,
                onToggle = { onChange(if (isSelected) selected - day else selected + day) },
            )
        }
    }
}

@Composable
private fun RowScope.DayChip(label: String, isSelected: Boolean, onToggle: () -> Unit) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1f)
            .clip(shape)
            .let { if (isSelected) it.background(Amber) else it.border(1.dp, ControlOutline, shape) }
            .clickable(onClick = onToggle)
            .padding(vertical = 9.dp),
    ) {
        Text(text = label, style = DataSmall, color = if (isSelected) OnAmber else TextMuted)
    }
}

@Composable
private fun PresetRow(selected: Set<DayOfWeek>, onChange: (Set<DayOfWeek>) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PresetChip("Todos los días", selected == EVERY_DAY) { onChange(EVERY_DAY) }
        PresetChip("Lun a Vie", selected == MON_TO_FRI) { onChange(MON_TO_FRI) }
    }
}

@Composable
private fun PresetChip(label: String, active: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    val color = if (active) Amber else TextMuted
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        modifier = Modifier
            .clip(shape)
            .border(1.dp, color, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
