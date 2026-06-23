package com.titos.lystra.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.titos.lystra.data.model.ShoppingItem
import com.titos.lystra.ui.theme.Dimens
import com.titos.lystra.ui.theme.StrikethroughStyle

/**
 * Shopping list item composable matching the mockup exactly.
 *
 * Two visual states:
 * - Pending: full opacity, normal text, unchecked checkbox
 * - Checked (No Carrinho): 60% opacity, strikethrough text, green filled checkbox
 *
 * Single tap toggles the state with animation.
 */
@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onToggle: (ShoppingItem) -> Unit,
    onDelete: (ShoppingItem) -> Unit = {},
    onLongClick: ((ShoppingItem) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val isChecked = item.isInCart

    // Animate opacity
    val alpha by animateFloatAsState(
        targetValue = if (isChecked) 0.6f else 1f,
        animationSpec = tween(300),
        label = "item_alpha"
    )

    // Animate checkbox scale on toggle
    var checkScale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = checkScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "check_scale"
    )

    val interactionSource = remember { MutableInteractionSource() }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete(item)
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    Color.Transparent
                },
                label = "swipe_color"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Remover",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ListItemHeight)
            .alpha(alpha)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                // Trigger bounce animation
                checkScale = 1.2f
                onToggle(item)
            },
        color = if (isChecked) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .scale(animatedScale)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isChecked) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                    .then(
                        if (!isChecked) {
                            Modifier.background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(4.dp)
                            )
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!isChecked) {
                    // Empty checkbox border
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.name,
                    style = if (isChecked) {
                        StrikethroughStyle.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Subtitle: quantity + unit + notes
                val subtitle = buildString {
                    append("${item.quantity} ${item.unit}")
                    if (item.notes.isNotBlank()) {
                        append(" • ${item.notes}")
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Drag handle (visible on hover/focus in mockup, always shown here for touch)
            Icon(
                imageVector = Icons.Outlined.DragIndicator,
                contentDescription = "Reordenar",
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
        }
    }
    }

    // Reset scale after animation
    LaunchedEffect(isChecked) {
        checkScale = 1f
    }
}
