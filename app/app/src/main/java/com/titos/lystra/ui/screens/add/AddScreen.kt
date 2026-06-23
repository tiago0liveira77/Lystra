package com.titos.lystra.ui.screens.add

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.titos.lystra.data.model.Product
import com.titos.lystra.data.repository.ShoppingRepository
import com.titos.lystra.ui.components.ProductCard
import com.titos.lystra.ui.theme.Dimens

/**
 * Quick Add screen matching the 03-Adicionar mockup.
 *
 * Top: Search bar with auto-suggestions dropdown.
 * Bottom: "Produtos Frequentes" 2-column card grid.
 */
@Composable
fun AddScreen(
    repository: ShoppingRepository,
    onNavigateToProductEdit: (String) -> Unit,
    viewModel: AddViewModel = viewModel { AddViewModel(repository) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scrollState = androidx.compose.foundation.rememberScrollState()

    // Show snackbar on successful add
    LaunchedEffect(uiState.addedMessage) {
        uiState.addedMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearAddedMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp,
                    start = Dimens.MarginEdge,
                    end = Dimens.MarginEdge,
                    bottom = Dimens.BottomNavHeight + 16.dp
                )
        ) {
            // Search Section
            OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Adicionar produto...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = null,
                            tint = if (uiState.isSearching) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearSearch() }) {
                                Icon(
                                    Icons.Outlined.Close,
                                    contentDescription = "Limpar pesquisa",
                                    tint = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onDone = {
                            if (uiState.searchQuery.isNotBlank()) {
                                viewModel.addNewProduct(uiState.searchQuery)
                                viewModel.clearSearch()
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        }
                    )
                )

            Spacer(modifier = Modifier.height(32.dp))

            // Frequent Products Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Produtos Frequentes",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { /* Show all */ }) {
                    Text(
                        "Ver todos",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2-column grid
            if (uiState.frequentProducts.isEmpty()) {
                // Empty state for frequent products
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Os seus produtos frequentes\naparecerão aqui",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        items = uiState.frequentProducts,
                        key = { it.id }
                    ) { product ->
                        ProductCard(
                            product = product,
                            onAddClick = { 
                                viewModel.quickAdd(it)
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            },
                            onEditClick = {
                                onNavigateToProductEdit(it.id)
                            }
                        )
                    }
                }
            }
        }

        // Overlay Dropdown
        if (uiState.searchQuery.isNotBlank() && uiState.isSearching) {
            SuggestionsDropdown(
                suggestions = uiState.suggestions,
                query = uiState.searchQuery,
                onSuggestionClick = { product ->
                    viewModel.quickAdd(product)
                    viewModel.clearSearch()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
                onCreateNewClick = { query ->
                    viewModel.addNewProduct(query)
                    viewModel.clearSearch()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp + 64.dp + 8.dp, // 16dp outer padding + 64dp textfield height + 8dp spacing
                        start = Dimens.MarginEdge,
                        end = Dimens.MarginEdge
                    )
                    .heightIn(max = 300.dp)
            )
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Dimens.BottomNavHeight + 16.dp)
        )
    }
}

@Composable
private fun SuggestionsDropdown(
    suggestions: List<Product>,
    query: String,
    onSuggestionClick: (Product) -> Unit,
    onCreateNewClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            suggestions.forEach { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSuggestionClick(product) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .heightIn(min = Dimens.TouchTarget),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    // Highlight matching text
                    val annotated = buildAnnotatedString {
                        val name = product.name
                        val queryLower = query.lowercase()
                        val nameLower = name.lowercase()
                        val matchIndex = nameLower.indexOf(queryLower)

                        if (matchIndex >= 0) {
                            append(name.substring(0, matchIndex))
                            withStyle(SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )) {
                                append(name.substring(matchIndex, matchIndex + query.length))
                            }
                            append(name.substring(matchIndex + query.length))
                        } else {
                            append(name)
                        }
                    }

                    Text(
                        text = annotated,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Outlined.NorthWest,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Create new item row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCreateNewClick(query) }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .heightIn(min = Dimens.TouchTarget),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "Criar novo item: \"$query\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Footer
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Text(
                    text = "Toque para adicionar diretamente",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
