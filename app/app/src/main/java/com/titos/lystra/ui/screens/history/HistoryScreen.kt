package com.titos.lystra.ui.screens.history

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.titos.lystra.data.model.ProductCategory
import com.titos.lystra.data.repository.ShoppingRepository
import com.titos.lystra.ui.components.CategoryChip
import com.titos.lystra.ui.components.HistoryListItem
import com.titos.lystra.ui.theme.Dimens

/**
 * History screen matching the 02-Historico mockup.
 *
 * - Sticky search bar + filter chips below top bar
 * - "Showing N past items" + Sort control
 * - Scrollable list of HistoryListItem with "+" buttons
 */
@Composable
fun HistoryScreen(
    repository: ShoppingRepository,
    viewModel: HistoryViewModel = viewModel { HistoryViewModel(repository) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val chipScrollState = rememberScrollState()

    val filters = listOf("All Time", "Freq Bought") +
            ProductCategory.entries
                .filter { it != ProductCategory.OUTROS }
                .map { it.displayName }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // Search & Filter Section (sticky)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.MarginEdge)
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Pesquisar histórico...",
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Outlined.Mic,
                            contentDescription = "Pesquisa por voz",
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips (horizontal scroll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(chipScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    CategoryChip(
                        label = filter,
                        isSelected = uiState.selectedFilter == filter,
                        onClick = { viewModel.selectFilter(filter) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Count + Sort
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mostrando ${uiState.filteredProducts.size} produtos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = { /* Sort toggle */ }) {
                    Text(
                        text = "Ordenar: Data",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        Icons.Outlined.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // History List
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Sem produtos no histórico",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.MarginEdge,
                    end = Dimens.MarginEdge,
                    bottom = Dimens.BottomNavHeight + 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.filteredProducts,
                    key = { it.id }
                ) { product ->
                    HistoryListItem(
                        product = product,
                        timeAgoText = HistoryViewModel.timeAgoString(product.lastPurchasedAt),
                        onAddClick = { viewModel.addToList(it) }
                    )
                }

                // Load more button
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(
                            onClick = { /* Load more */ },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                "Carregar mais histórico",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
