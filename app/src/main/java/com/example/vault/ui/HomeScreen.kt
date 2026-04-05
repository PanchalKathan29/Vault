package com.example.vault.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vault.R
import com.example.vault.data.FinanceCategories
import com.example.vault.data.Transaction
import com.example.vault.ui.theme.VaultBalancePillOverlay
import com.example.vault.ui.theme.VaultChartBar
import com.example.vault.ui.theme.VaultChartCardBackground
import com.example.vault.ui.theme.VaultEmptyIllustrationCircle
import com.example.vault.ui.theme.VaultEmptyIllustrationIcon
import com.example.vault.ui.theme.VaultLabelLight
import com.example.vault.ui.theme.VaultNavy
import com.example.vault.ui.theme.VaultNavyCardEnd
import com.example.vault.ui.theme.VaultNavyCardStart
import com.example.vault.ui.theme.VaultScreenBackground
import com.example.vault.ui.theme.VaultSubtextGray
import com.example.vault.ui.theme.VaultSurfaceWhite
import com.example.vault.ui.theme.VaultTealAccent
import com.example.vault.ui.theme.VaultTealSoft
import com.example.vault.ui.theme.VaultTransactionRowIconBackground
import com.example.vault.utils.HelperUtil.formatTransactionDate
import com.example.vault.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(viewModel: TransactionViewModel) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()

    val currentBalance = totalIncome - totalExpense
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    val recentThree = remember(transactions) {
        transactions.sortedByDescending { it.dateMillis }.take(3)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VaultScreenBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { HomeHeader() }

        item {
            BalanceCard(
                currentBalance = currentBalance,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                formatMoney = { currencyFormat.format(it) }
            )
        }

        if (transactions.isEmpty()) {
            item { EmptyHomeBody() }
        } else {
            item {
                PopulatedHomeBody(
                    recentTransactions = recentThree,
                    formatMoney = { currencyFormat.format(it) }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = stringResource(R.string.cd_profile),
                modifier = Modifier.size(40.dp),
                tint = VaultNavy
            )
            Column {
                Text(
                    text = stringResource(R.string.home_greeting),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,
                    color = VaultSubtextGray
                )
                Text(
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = VaultNavy,
                        fontSize = 18.sp
                    )
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.NotificationsNone,
            contentDescription = stringResource(R.string.cd_notifications),
            modifier = Modifier.size(26.dp),
            tint = VaultNavy
        )
    }
}

@Composable
private fun BalanceCard(
    currentBalance: Double,
    totalIncome: Double,
    totalExpense: Double,
    formatMoney: (Double) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(VaultNavyCardStart, VaultNavyCardEnd)
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.home_current_balance),
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    color = VaultLabelLight,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = stringResource(R.string.home_currency_symbol),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(bottom = 4.dp, end = 2.dp)
                )
                Text(
                    text = formatMoney(currentBalance).removePrefix("$"),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 40.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IncomeExpensePill(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.home_total_income),
                    amount = formatMoney(totalIncome)
                )
                IncomeExpensePill(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.home_total_expenses),
                    amount = formatMoney(totalExpense)
                )
            }
        }
    }
}

@Composable
private fun IncomeExpensePill(
    modifier: Modifier = Modifier,
    label: String,
    amount: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VaultBalancePillOverlay)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun EmptyHomeBody() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VaultSurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(VaultTealSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = VaultTealAccent,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.home_welcome_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = VaultNavy
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.home_welcome_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = VaultSubtextGray
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(32.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(VaultEmptyIllustrationCircle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ReceiptLong,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = VaultEmptyIllustrationIcon
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.home_empty_title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = VaultNavy
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.home_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = VaultSubtextGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun PopulatedHomeBody(
    recentTransactions: List<Transaction>,
    formatMoney: (Double) -> String
) {
    NoSpendStreakCard()
    Spacer(modifier = Modifier.height(16.dp))
    SevenDayActivitySection()
    Spacer(modifier = Modifier.height(20.dp))
    RecentTransactionsSection(
        transactions = recentTransactions,
        formatMoney = formatMoney
    )
}

@Composable
private fun NoSpendStreakCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VaultSurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(VaultTealAccent)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VaultTealSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = VaultTealAccent,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_streak_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = VaultNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.home_streak_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = VaultSubtextGray
                    )
                }
                Text(
                    text = stringResource(R.string.home_streak_days),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = VaultNavy
                    )
                )
            }
        }
    }
}

@Composable
private fun SevenDayActivitySection() {
    Text(
        text = stringResource(R.string.home_activity_7day),
        style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.sp,
            color = VaultSubtextGray,
            fontWeight = FontWeight.Medium
        ),
        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VaultChartCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        val heights = listOf(0.35f, 0.55f, 0.4f, 0.75f, 0.5f, 0.9f, 0.45f)
        val maxBar = 120.dp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            heights.forEach { fraction ->
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(maxBar * fraction)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(VaultChartBar)
                )
            }
        }
    }
}

@Composable
private fun RecentTransactionsSection(
    transactions: List<Transaction>,
    formatMoney: (Double) -> String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.home_recent_transactions),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = VaultNavy
            )
        )
        Text(
            text = stringResource(R.string.home_see_all),
            style = MaterialTheme.typography.labelLarge,
            color = VaultSubtextGray
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        transactions.forEach { tx ->
            TransactionRow(
                transaction = tx,
                formatMoney = formatMoney
            )
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    formatMoney: (Double) -> String
) {
    val todayTemplate = stringResource(R.string.home_transaction_today_time)
    val yesterdayTemplate = stringResource(R.string.home_transaction_yesterday_time)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VaultSurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(VaultTransactionRowIconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = FinanceCategories.iconForStoredCategory(transaction.category),
                    contentDescription = null,
                    tint = VaultNavy,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = VaultNavy
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatTransactionDate(
                        transaction.dateMillis,
                        todayTemplate,
                        yesterdayTemplate
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = VaultSubtextGray
                )
            }
            val isExpense = transaction.type.equals("Expense", ignoreCase = true)
            val amountText = if (isExpense) {
                "-${formatMoney(transaction.amount)}"
            } else {
                "+${formatMoney(transaction.amount)}"
            }
            Text(
                text = amountText,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = VaultNavy
                )
            )
        }
    }
}
