package com.example.vault.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vault.R
import com.example.vault.ui.theme.VaultNavy
import com.example.vault.ui.theme.VaultScreenBackground
import com.example.vault.ui.theme.VaultSubtextGray
import com.example.vault.ui.theme.VaultSurfaceWhite
import com.example.vault.utils.HelperUtil.buildExpenseBreakdown
import com.example.vault.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

@Composable
fun InsightsScreen(viewModel: TransactionViewModel) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    val expenseBreakdown = remember(transactions) {
        buildExpenseBreakdown(transactions)
    }
    val totalSpent = expenseBreakdown.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VaultScreenBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.insights_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = VaultNavy
                )
            )
            Text(
                text = stringResource(R.string.insights_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = VaultSubtextGray
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.insights_total_spent_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            color = VaultSubtextGray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currencyFormat.format(totalSpent),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = VaultNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    if (expenseBreakdown.isEmpty()) {
                        Text(
                            text = stringResource(R.string.insights_empty_chart),
                            style = MaterialTheme.typography.bodyMedium,
                            color = VaultSubtextGray,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    } else {
                        ExpenseDonutChart(
                            segments = expenseBreakdown.map {
                                Triple(it.color, it.amount.toFloat(), it.label)
                            },
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.insights_by_category),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    color = VaultSubtextGray,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        items(expenseBreakdown, key = { it.label }) { row ->
            CategoryBreakdownRow(
                label = row.label,
                amount = row.amount,
                percent = if (totalSpent > 0) row.amount / totalSpent else 0.0,
                color = row.color,
                formatMoney = { currencyFormat.format(it) }
            )
        }
    }
}

@Composable
private fun ExpenseDonutChart(
    segments: List<Triple<Color, Float, String>>,
    modifier: Modifier = Modifier
) {
    val total = segments.sumOf { it.second.toDouble() }.toFloat()
    val safeTotal = max(total, 0.0001f)
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = size.minDimension * 0.14f
            val diameter = size.minDimension - stroke
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            var startAngle = -90f
            segments.forEach { (color, value, _) ->
                val sweep = value / safeTotal * 360f
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = Stroke(width = stroke, cap = StrokeCap.Butt)
                )
                startAngle += sweep
            }
        }
    }
}

@Composable
private fun CategoryBreakdownRow(
    label: String,
    amount: Double,
    percent: Double,
    color: Color,
    formatMoney: (Double) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VaultSurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = VaultNavy
                    )
                )
                Text(
                    text = stringResource(
                        R.string.insights_percent_of_spending,
                        (percent * 100).toInt()
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = VaultSubtextGray
                )
            }
            Text(
                text = formatMoney(amount),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = VaultNavy
                )
            )
        }
    }
}
