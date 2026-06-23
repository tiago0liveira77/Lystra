package com.titos.lystra.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.titos.lystra.MainActivity

/**
 * Home screen widget showing pending shopping list items.
 * Displays the first few items with a quick-add button.
 */
class ShoppingListWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            ShoppingListWidgetContent()
        }
    }
}

@Composable
private fun ShoppingListWidgetContent() {
    // Use Android Color resources for Glance compatibility
    val primaryColor = ColorProvider(android.graphics.Color.parseColor("#0D631B"))
    val textColor = ColorProvider(android.graphics.Color.parseColor("#1B1C1C"))
    val subtextColor = ColorProvider(android.graphics.Color.parseColor("#40493D"))

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(android.graphics.Color.WHITE))
            .cornerRadius(16.dp)
            .padding(16.dp)
            .clickable(actionStartActivity<MainActivity>()),
        verticalAlignment = Alignment.Top,
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🛒 Lista de Compras",
                style = TextStyle(
                    color = primaryColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.defaultWeight()
            )
        }

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Placeholder items (in a real implementation, these would come from
        // a WorkManager query to Firestore)
        Text(
            text = "Toque para abrir a sua lista",
            style = TextStyle(
                color = subtextColor,
                fontSize = 14.sp,
            )
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        Text(
            text = "Os itens pendentes aparecerão aqui",
            style = TextStyle(
                color = subtextColor,
                fontSize = 12.sp,
            )
        )
    }
}

/**
 * Widget receiver that creates the ShoppingListWidget.
 */
class ShoppingListWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ShoppingListWidget()
}
