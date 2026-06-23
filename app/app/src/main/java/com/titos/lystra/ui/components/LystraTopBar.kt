package com.titos.lystra.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.titos.lystra.ui.theme.Dimens

/**
 * Top App Bar matching the FreshCart Logic mockups.
 * Sync icon (left) — "FreshCart Logic" title (center) — Cloud status (right).
 */
@Composable
fun LystraTopBar(
    modifier: Modifier = Modifier,
    onSyncClick: () -> Unit = {},
    onCloudClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.TopBarHeight)
                .padding(horizontal = Dimens.MarginEdge),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sync button
            IconButton(
                onClick = onSyncClick,
                modifier = Modifier.size(Dimens.TouchTarget)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Sync,
                    contentDescription = "Sincronizar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // App title
            Text(
                text = "FreshCart Logic",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Cloud status
            IconButton(
                onClick = onCloudClick,
                modifier = Modifier.size(Dimens.TouchTarget)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudDone,
                    contentDescription = "Estado da nuvem",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
