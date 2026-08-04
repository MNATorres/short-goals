package com.mnatorres.shortgoals.app.ui.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mnatorres.shortgoals.app.ui.theme.Amber
import com.mnatorres.shortgoals.app.ui.theme.PanelBorder
import com.mnatorres.shortgoals.app.ui.theme.TextMuted
import com.mnatorres.shortgoals.core.HeatCell
import com.mnatorres.shortgoals.core.HeatLevel
import java.time.LocalDate

private val HeatNone = Color(0xFF262B33)
private val HeatLow = Color(0xFF4A3E1E)
private val HeatMedium = Color(0xFF8A6C24)
private const val WEEK_LENGTH = 7

@Composable
internal fun MonthHeatmap(cells: List<HeatCell>, onOpenDay: (LocalDate) -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, PanelBorder),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(12.dp),
        ) {
            // Monday-first alignment: pad the first week with empty slots.
            val offset = cells.firstOrNull()?.date?.dayOfWeek?.value?.minus(1) ?: 0
            val slots: List<HeatCell?> = List(offset) { null } + cells
            slots.chunked(WEEK_LENGTH).forEach { week ->
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    week.forEach { cell ->
                        HeatCellBox(cell, onOpenDay, Modifier.weight(1f))
                    }
                    repeat(WEEK_LENGTH - week.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeatCellBox(cell: HeatCell?, onOpenDay: (LocalDate) -> Unit, modifier: Modifier) {
    if (cell == null) {
        Spacer(modifier.aspectRatio(1f))
        return
    }
    val shape = RoundedCornerShape(4.dp)
    val color = when (cell.level) {
        HeatLevel.NONE -> HeatNone
        HeatLevel.LOW -> HeatLow
        HeatLevel.MEDIUM -> HeatMedium
        HeatLevel.FULL -> Amber
    }
    val pendingReview = cell.scheduled > 0 && !cell.closed
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(color)
            .let { if (pendingReview) it.border(1.dp, TextMuted, shape) else it }
            .clickable { onOpenDay(cell.date) },
    )
}

@Composable
internal fun HeatmapHint() {
    Text(
        text = "Tocá un día para abrirlo · borde = sin cerrar",
        style = MaterialTheme.typography.bodySmall,
        color = TextMuted,
    )
}
