package com.example.vault.utils

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import com.example.vault.data.FinanceCategories
import com.example.vault.data.Transaction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ExpenseSlice(
    val label: String,
    val amount: Double,
    val color: Color
)

object HelperUtil {

    fun formatDateChip(millis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        return when {
            isSameDay(cal, today) -> "Today"
            isSameDay(cal, yesterday) -> "Yesterday"
            else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(millis))
        }
    }

    fun isSameDay(a: Calendar, b: Calendar): Boolean {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }

    fun buildExpenseBreakdown(transactions: List<Transaction>): List<ExpenseSlice> {
        val now = Calendar.getInstance()
        val y = now.get(Calendar.YEAR)
        val m = now.get(Calendar.MONTH)
        val expenses = transactions.filter { it.type.equals("Expense", ignoreCase = true) }
            .filter { tx ->
                val c = Calendar.getInstance().apply { timeInMillis = tx.dateMillis }
                c.get(Calendar.YEAR) == y && c.get(Calendar.MONTH) == m
            }
        val grouped = expenses.groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
            .filter { it.value > 0 }

        val ordered = FinanceCategories.all.mapNotNull { cat ->
            val amt = grouped[cat.displayName] ?: return@mapNotNull null
            ExpenseSlice(cat.displayName, amt, cat.chartColor)
        }.toMutableList()

        val knownNames = FinanceCategories.all.map { it.displayName }.toSet()
        grouped.forEach { (name, amt) ->
            if (name !in knownNames) {
                ordered.add(
                    ExpenseSlice(
                        label = name,
                        amount = amt,
                        color = FinanceCategories.colorForStoredCategory(name)
                    )
                )
            }
        }

        return ordered.sortedByDescending { it.amount }
    }

    @SuppressLint("SimpleDateFormat")
    fun formatTransactionDate(
        dateMillis: Long,
        todayTemplate: String,
        yesterdayTemplate: String
    ): String {
        val txCal = Calendar.getInstance().apply { timeInMillis = dateMillis }
        val now = Calendar.getInstance()
        val timeFmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val time = timeFmt.format(Date(dateMillis))
        val yesterdayCal = Calendar.getInstance().apply {
            timeInMillis = now.timeInMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return when {
            isSameDay(txCal, now) -> String.format(Locale.getDefault(), todayTemplate, time)
            isSameDay(txCal, yesterdayCal) -> String.format(Locale.getDefault(), yesterdayTemplate, time)
            else -> SimpleDateFormat("MMM d, yyyy • hh:mm a", Locale.getDefault()).format(Date(dateMillis))
        }
    }

}