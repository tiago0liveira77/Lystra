package com.titos.lystra.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.titos.lystra.ui.theme.*

/**
 * Supermarket product categories with Portuguese display names,
 * Material icons, and pastel tint colors for visual grouping.
 */
enum class ProductCategory(
    val displayName: String,
    val icon: ImageVector,
    val tintColor: Color,
    val onTintColor: Color
) {
    LATICINIOS(
        displayName = "Laticínios",
        icon = Icons.Outlined.WaterDrop,
        tintColor = CategoryDairy,
        onTintColor = CategoryDairyDark
    ),
    FRUTAS_VEGETAIS(
        displayName = "Frutas e Vegetais",
        icon = Icons.Outlined.Eco,
        tintColor = CategoryFruits,
        onTintColor = CategoryFruitsDark
    ),
    PADARIA(
        displayName = "Padaria",
        icon = Icons.Outlined.BakeryDining,
        tintColor = CategoryBakery,
        onTintColor = CategoryBakeryDark
    ),
    CARNES(
        displayName = "Carnes e Peixe",
        icon = Icons.Outlined.SetMeal,
        tintColor = CategoryMeat,
        onTintColor = CategoryMeatDark
    ),
    CONGELADOS(
        displayName = "Congelados",
        icon = Icons.Outlined.AcUnit,
        tintColor = CategoryFrozen,
        onTintColor = CategoryFrozenDark
    ),
    LIMPEZA(
        displayName = "Limpeza",
        icon = Icons.Outlined.CleaningServices,
        tintColor = CategoryCleaning,
        onTintColor = CategoryCleaningDark
    ),
    BEBIDAS(
        displayName = "Bebidas",
        icon = Icons.Outlined.LocalCafe,
        tintColor = CategoryDrinks,
        onTintColor = CategoryDrinksDark
    ),
    MERCEARIA(
        displayName = "Mercearia",
        icon = Icons.Outlined.ShoppingBasket,
        tintColor = CategoryPantry,
        onTintColor = CategoryPantryDark
    ),
    HIGIENE(
        displayName = "Higiene",
        icon = Icons.Outlined.Spa,
        tintColor = CategoryHygiene,
        onTintColor = CategoryHygieneDark
    ),
    OUTROS(
        displayName = "Outros",
        icon = Icons.Outlined.ShoppingCart,
        tintColor = CategoryOther,
        onTintColor = CategoryOtherDark
    );

    companion object {
        fun fromDisplayName(name: String): ProductCategory {
            return entries.find {
                it.displayName.equals(name, ignoreCase = true)
            } ?: OUTROS
        }

        fun fromKey(key: String): ProductCategory {
            return try {
                valueOf(key.uppercase())
            } catch (_: Exception) {
                OUTROS
            }
        }
    }
}
