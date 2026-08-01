package com.mnatorres.shortgoals.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector

enum class Tab(val label: String, val icon: ImageVector) {
    Today("Hoy", Icons.Filled.CheckCircle),
    Goals("Objetivos", Icons.AutoMirrored.Filled.List),
    Progress("Progreso", Icons.Filled.DateRange),
}
