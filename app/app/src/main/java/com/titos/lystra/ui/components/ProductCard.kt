package com.titos.lystra.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.titos.lystra.data.model.Product
import com.titos.lystra.data.model.ProductCategory

/**
 * Product card for the "Produtos Frequentes" grid on the Add screen.
 * Matches the 2-column card layout from the mockup.
 *
 * Shows: product name, category icon, category label, green "+" button.
 */
@Composable
fun ProductCard(
    product: Product,
    onAddClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    val category = ProductCategory.fromDisplayName(product.category)

    var addScale by remember { mutableFloatStateOf(1f) }
    val animatedAddScale by animateFloatAsState(
        targetValue = addScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "add_scale"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        border = null,
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .heightIn(min = 100.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row: Name + Category icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Category icon circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom row: Category label + Add button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                // Green "+" button
                FilledIconButton(
                    onClick = {
                        addScale = 0.85f
                        onAddClick(product)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .scale(animatedAddScale),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Adicionar ${product.name}",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    // Reset scale
    LaunchedEffect(addScale) {
        if (addScale != 1f) {
            addScale = 1f
        }
    }
}
