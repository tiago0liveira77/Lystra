package com.titos.lystra.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.titos.lystra.ui.theme.Dimens

/**
 * Large ergonomic quantity selector from the Edit screen mockup.
 * Minus button (64dp) — Number display (centered) — Plus button (64dp, primary).
 * Includes scale bounce animation on tap.
 */
@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    maxQuantity: Int = 99,
) {
    var bounceTarget by remember { mutableFloatStateOf(1f) }
    val bounceScale by animateFloatAsState(
        targetValue = bounceTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "qty_bounce"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "QUANTITY",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minus button
                FilledIconButton(
                    onClick = {
                        if (quantity > minQuantity) {
                            bounceTarget = 1.1f
                            onQuantityChange(quantity - 1)
                        }
                    },
                    modifier = Modifier.size(Dimens.QuantityButtonSize),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                    shape = CircleShape,
                    enabled = quantity > minQuantity
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Diminuir quantidade",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Quantity display
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(96.dp)
                        .scale(bounceScale)
                )

                // Plus button (primary, prominent)
                FilledIconButton(
                    onClick = {
                        if (quantity < maxQuantity) {
                            bounceTarget = 1.1f
                            onQuantityChange(quantity + 1)
                        }
                    },
                    modifier = Modifier
                        .size(Dimens.QuantityButtonSize)
                        .shadow(4.dp, CircleShape),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    shape = CircleShape,
                    enabled = quantity < maxQuantity
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Aumentar quantidade",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }

    // Reset bounce
    LaunchedEffect(bounceTarget) {
        if (bounceTarget != 1f) {
            bounceTarget = 1f
        }
    }
}

// Needed for the sp literal in letterSpacing
private val Float.sp: androidx.compose.ui.unit.TextUnit
    get() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)

private val Double.sp: androidx.compose.ui.unit.TextUnit
    get() = this.toFloat().sp
