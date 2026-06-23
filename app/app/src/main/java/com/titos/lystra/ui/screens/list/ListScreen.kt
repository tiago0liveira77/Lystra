package com.titos.lystra.ui.screens.list

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.titos.lystra.data.repository.ShoppingRepository
import com.titos.lystra.ui.components.ShoppingListItem
import com.titos.lystra.ui.theme.Dimens
import com.titos.lystra.ui.theme.FABShape

/**
 * Shopping List screen matching the 01-Lista mockup.
 *
 * Shows pending items grouped by category, then a collapsible
 * "No Carrinho" section at the bottom with reduced opacity.
 */
@Composable
fun ListScreen(
    repository: ShoppingRepository,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: ListViewModel = viewModel { ListViewModel(repository) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var isCartExpanded by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isEmpty && !uiState.isLoading) {
            // Empty state
            EmptyListState(
                onAddClick = onNavigateToAdd,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = Dimens.FABClearance + Dimens.BottomNavHeight + 16.dp,
                    start = Dimens.MarginEdge,
                    end = Dimens.MarginEdge
                ),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Loading indicator
                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Pending items grouped by category
                uiState.pendingByCategory.forEach { (category, items) ->
                    // Category header
                    item(key = "header_$category") {
                        Text(
                            text = category.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(
                                top = 24.dp,
                                bottom = 12.dp
                            )
                        )
                    }

                    // Items in card container
                    item(key = "card_$category") {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 0.5.dp,
                            border = null,
                        ) {
                            Column {
                                items.forEachIndexed { index, shoppingItem ->
                                    key(shoppingItem.id) {
                                        ShoppingListItem(
                                            item = shoppingItem,
                                            onToggle = { viewModel.toggleItem(it) },
                                            onDelete = { viewModel.removeItem(it) },
                                            onLongClick = { onNavigateToEdit(it.id) }
                                        )
                                        if (index < items.lastIndex) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(start = 56.dp),
                                                thickness = 0.5.dp,
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                                    .copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // "No Carrinho" section
                if (uiState.cartItems.isNotEmpty()) {
                    item(key = "cart_divider") {
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                                .copy(alpha = 0.5f)
                        )
                    }

                    item(key = "cart_header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NO CARRINHO (${uiState.cartCount})",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.outline,
                                letterSpacing = 1.5.sp,
                            )

                            TextButton(
                                onClick = { isCartExpanded = !isCartExpanded }
                            ) {
                                Text(
                                    text = if (isCartExpanded) "Ocultar" else "Mostrar",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    if (isCartExpanded) {
                        item(key = "cart_items") {
                            Surface(
                                modifier = Modifier.alpha(0.6f),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Column {
                                    uiState.cartItems.forEachIndexed { index, cartItem ->
                                        key(cartItem.id) {
                                            ShoppingListItem(
                                                item = cartItem,
                                                onToggle = { viewModel.toggleItem(it) },
                                                onDelete = { viewModel.removeItem(it) }
                                            )
                                            if (index < uiState.cartItems.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(start = 56.dp),
                                                    thickness = 0.5.dp,
                                                    color = MaterialTheme.colorScheme.surfaceVariant
                                                        .copy(alpha = 0.3f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onNavigateToAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = Dimens.MarginEdge,
                    bottom = Dimens.BottomNavHeight + 16.dp
                )
                .size(Dimens.FABSize),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = FABShape,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Adicionar item",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun EmptyListState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "A sua lista está vazia",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Adicione o primeiro produto para começar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        FilledTonalButton(
            onClick = onAddClick,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Adicionar Produto", fontWeight = FontWeight.SemiBold)
        }
    }
}

// Needed for sp literal
private val Float.sp: androidx.compose.ui.unit.TextUnit
    get() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)

private val Double.sp: androidx.compose.ui.unit.TextUnit
    get() = this.toFloat().sp
