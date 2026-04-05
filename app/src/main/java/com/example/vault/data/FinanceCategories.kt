package com.example.vault.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class FinanceCategory(
    val id: String,
    val displayName: String,
    val chartColor: Color,
    val icon: ImageVector
)

/**
 * Single source of truth for category labels used in New Transaction, Home, and Insights.
 */
object FinanceCategories {

    val all: List<FinanceCategory> = listOf(
        FinanceCategory("food", "Food & Dining", Color(0xFFFF7043), Icons.Filled.Restaurant),
        FinanceCategory("groceries", "Groceries", Color(0xFF66BB6A), Icons.Filled.ShoppingBasket),
        FinanceCategory("coffee", "Coffee & Snacks", Color(0xFF8D6E63), Icons.Filled.LocalCafe),
        FinanceCategory("transport", "Transport", Color(0xFF42A5F5), Icons.Filled.DirectionsBus),
        FinanceCategory("shopping", "Shopping", Color(0xFFAB47BC), Icons.Filled.Storefront),
        FinanceCategory("entertainment", "Entertainment", Color(0xFFEC407A), Icons.Filled.Movie),
        FinanceCategory("bills", "Bills & Utilities", Color(0xFF78909C), Icons.Filled.Home),
        FinanceCategory("health", "Health & Fitness", Color(0xFF26A69A), Icons.Filled.FitnessCenter),
        FinanceCategory("work", "Work & Business", Color(0xFF5C6BC0), Icons.Filled.Work),
        FinanceCategory("other", "Other", Color(0xFF90A4AE), Icons.Filled.Payments)
    )

    fun iconForStoredCategory(displayName: String): ImageVector {
        val match = all.find { it.displayName.equals(displayName, ignoreCase = true) }
        if (match != null) return match.icon
        val c = displayName.lowercase()
        return when {
            "coffee" in c || "cafe" in c -> Icons.Filled.LocalCafe
            "transport" in c || "bus" in c -> Icons.Filled.DirectionsBus
            "grocery" in c -> Icons.Filled.ShoppingBasket
            else -> Icons.Filled.Payments
        }
    }

    fun colorForStoredCategory(displayName: String): Color {
        return all.find { it.displayName.equals(displayName, ignoreCase = true) }?.chartColor
            ?: Color(0xFF90A4AE)
    }
}
