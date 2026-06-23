package com.titos.lystra.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 3-column grid of unit buttons matching the Edit screen mockup.
 * Single-select behavior: tapping a unit deselects the previous.
 *
 * Available units: un, kg, l, g, caixa, pacote
 */
@Composable
fun UnitGrid(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val units = listOf("un", "kg", "l", "g", "caixa", "pacote")

    Column(modifier = modifier) {
        Text(
            text = "UNIT",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        // 3-column grid
        units.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { unit ->
                    UnitButton(
                        unit = unit,
                        isSelected = selectedUnit == unit,
                        onClick = { onUnitSelected(unit) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty cells if row is incomplete
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun UnitButton(
    unit: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = tween(200),
        label = "unit_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(200),
        label = "unit_text"
    )

    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        shadowElevation = if (isSelected) 1.dp else 0.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = unit,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Needed for the sp literal in letterSpacing
private val Float.sp: androidx.compose.ui.unit.TextUnit
    get() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)

private val Double.sp: androidx.compose.ui.unit.TextUnit
    get() = this.toFloat().sp
