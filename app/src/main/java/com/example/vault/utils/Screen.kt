package com.example.vault.utils

import androidx.annotation.StringRes
import com.example.vault.R

sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: Int
) {
    object Home : Screen("home", R.string.nav_home, R.drawable.ic_home)
    object Add : Screen("add", R.string.nav_add, R.drawable.ic_add)
    object Insights : Screen("insights", R.string.nav_insights, R.drawable.ic_insight)
}