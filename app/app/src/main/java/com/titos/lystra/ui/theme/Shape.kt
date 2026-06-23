package com.titos.lystra.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ============================================================================
// Shape System — "Large Radii" for friendly, modern feel
// Matching DESIGN.md specifications
// ============================================================================
val LystraShapes = Shapes(
    // 4dp — Checkboxes, small indicators
    extraSmall = RoundedCornerShape(4.dp),

    // 8dp — Default rounding
    small = RoundedCornerShape(8.dp),

    // 12dp — Input fields, medium containers
    medium = RoundedCornerShape(12.dp),

    // 16dp — Cards, list containers, main surfaces
    large = RoundedCornerShape(16.dp),

    // 24dp — Bottom sheets (top corners), modals
    extraLarge = RoundedCornerShape(24.dp),
)

// Additional shape constants for specific components
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val PillShape = RoundedCornerShape(percent = 50)
val FABShape = RoundedCornerShape(16.dp)
