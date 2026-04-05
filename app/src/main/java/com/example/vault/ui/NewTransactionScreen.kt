package com.example.vault.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.vault.data.FinanceCategories
import com.example.vault.data.FinanceCategory
import com.example.vault.ui.theme.*
import com.example.vault.utils.HelperUtil.formatDateChip
import com.example.vault.utils.Screen
import com.example.vault.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

private const val MAX_CENTS = 99_999_999L // $999,999.99

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTransactionScreen(
    viewModel: TransactionViewModel,
    navController: NavHostController
) {
    // --- State Management ---
    var amountCents by remember { mutableLongStateOf(0L) }
    var isExpense by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<FinanceCategory?>(null) }
    var showCategorySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var dateMillis by remember {
        mutableLongStateOf(
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        )
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var showKeypad by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)

    LaunchedEffect(showDatePicker) {
        if (showDatePicker) datePickerState.selectedDateMillis = dateMillis
    }

    fun navigateToHome() {
        navController.navigate(Screen.Home.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun onSaveClick() {
        if (selectedCategory == null) {
            scope.launch { snackbarHostState.showSnackbar("Select a category") }
            return
        }
        if (amountCents <= 0L) {
            scope.launch { snackbarHostState.showSnackbar("Enter an amount") }
            return
        }
        viewModel.addTransaction(
            amount = amountCents / 100.0,
            type = if (isExpense) "Expense" else "Income",
            category = selectedCategory!!.displayName,
            dateMillis = dateMillis,
            notes = notes
        )
        navigateToHome()
    }

    // --- Main UI Layout ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VaultScreenBackground)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    showKeypad = false
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopNavigationBar(
                onBackClick = { navigateToHome() },
                onSaveClick = { onSaveClick() }
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                item { TransactionTypeToggle(isExpense = isExpense, onTypeChange = { isExpense = it }) }

                item {
                    AmountDisplay(
                        amountCents = amountCents,
                        showKeypad = showKeypad,
                        currencyFormat = currencyFormat,
                        onClick = {
                            focusManager.clearFocus()
                            showKeypad = true
                        }
                    )
                }

                item {
                    CategorySelectorCard(
                        selectedCategory = selectedCategory,
                        onClick = {
                            showKeypad = false
                            focusManager.clearFocus()
                            showCategorySheet = true
                        }
                    )
                }

                item {
                    DateAndRecurringRow(
                        dateMillis = dateMillis,
                        onDateClick = {
                            showKeypad = false
                            focusManager.clearFocus()
                            showDatePicker = true
                        },
                        onRecurringClick = {
                            showKeypad = false
                            focusManager.clearFocus()
                            scope.launch { snackbarHostState.showSnackbar("Recurring rules coming soon") }
                        }
                    )
                }

                item {
                    NotesSection(
                        notes = notes,
                        onNotesChange = { notes = it },
                        onFocusChanged = { isFocused ->
                            if (isFocused) showKeypad = false
                        }
                    )
                }
            }

            AnimatedVisibility(visible = showKeypad) {
                AmountKeypad(
                    onDigit = { amountCents = (amountCents * 10 + it).coerceAtMost(MAX_CENTS) },
                    onBackspace = { amountCents /= 10 },
                    onDecimal = {
                        scope.launch { snackbarHostState.showSnackbar("Just type the numbers! Decimals are added automatically.") }
                    },
                    onDone = { showKeypad = false }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }

    // --- Dialogs & Bottom Sheets ---
    CategoryBottomSheet(
        showSheet = showCategorySheet,
        sheetState = sheetState,
        selectedCategory = selectedCategory,
        onCategorySelected = { cat ->
            selectedCategory = cat
            scope.launch {
                sheetState.hide()
                showCategorySheet = false
            }
        },
        onDismissRequest = { showCategorySheet = false }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { picked ->
                            val cal = Calendar.getInstance().apply { timeInMillis = picked }
                            cal.set(Calendar.HOUR_OF_DAY, 12); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                            dateMillis = cal.timeInMillis
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun TopNavigationBar(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VaultNavyTitle)
        }
        Text(
            text = "New Transaction",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = VaultNavyTitle)
        )
        Button(
            onClick = onSaveClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = VaultNavy),
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text("Save", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TransactionTypeToggle(
    isExpense: Boolean,
    onTypeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(VaultToggleInactiveBackground)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(if (isExpense) VaultNavy else Color.Transparent)
                .clickable { onTypeChange(true) }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Expense",
                fontWeight = FontWeight.SemiBold,
                color = if (isExpense) Color.White else VaultToggleInactiveText
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(if (!isExpense) VaultNavy else Color.Transparent)
                .clickable { onTypeChange(false) }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Income",
                fontWeight = FontWeight.SemiBold,
                color = if (!isExpense) Color.White else VaultToggleInactiveText
            )
        }
    }
}

@Composable
private fun AmountDisplay(
    amountCents: Long,
    showKeypad: Boolean,
    currencyFormat: NumberFormat,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AMOUNT",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.2.sp, color = VaultLabelMuted, fontWeight = FontWeight.Medium
            )
        )
        Text(
            text = currencyFormat.format(amountCents / 100.0),
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (showKeypad) VaultTealAccent else VaultNavy,
                fontSize = 44.sp
            )
        )
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(if (showKeypad) 2.dp else 1.dp)
                    .background(if (showKeypad) VaultTealAccent else Color(0xFFE0E4EB))
            )
        }
    }
}

@Composable
private fun CategorySelectorCard(
    selectedCategory: FinanceCategory?,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = "CATEGORY",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, color = VaultLabelMuted)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(VaultTealSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Apps, contentDescription = null, tint = VaultTealAccent, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = selectedCategory?.displayName ?: "Select Category",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedCategory == null) VaultLabelMuted else VaultNavyTitle
                )
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Select Category", tint = VaultLabelMuted)
            }
        }
    }
}

@Composable
private fun DateAndRecurringRow(
    dateMillis: Long,
    onDateClick: () -> Unit,
    onRecurringClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "DATE",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, color = VaultLabelMuted)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onDateClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = VaultLabelMuted, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = formatDateChip(dateMillis), style = MaterialTheme.typography.bodyMedium, color = VaultNavyTitle)
                }
            }
        }
    }
}

@Composable
private fun NotesSection(
    notes: String,
    onNotesChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "NOTES",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, color = VaultLabelMuted)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .onFocusChanged { focusState -> onFocusChanged(focusState.isFocused) },
                placeholder = { Text("Add a note...", color = VaultLabelMuted.copy(alpha = 0.8f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryBottomSheet(
    showSheet: Boolean,
    sheetState: SheetState,
    selectedCategory: FinanceCategory?,
    onCategorySelected: (FinanceCategory) -> Unit,
    onDismissRequest: () -> Unit
) {
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = VaultScreenBackground
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                Text(
                    text = "Select Category",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = VaultNavyTitle),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(FinanceCategories.all) { cat ->
                        val isSelected = selectedCategory == cat
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCategorySelected(cat) }
                                .background(if (isSelected) VaultTealSoft.copy(alpha = 0.5f) else Color.Transparent)
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = cat.displayName,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = VaultNavyTitle
                                )
                            )
                            if (isSelected) Icon(Icons.Default.Check, contentDescription = "Selected", tint = VaultTealAccent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AmountKeypad(
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onDecimal: () -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F2F8))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Done",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = VaultTealAccent),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDone() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(".", "0", "⌫")
        )
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                when (key) {
                                    "⌫" -> onBackspace()
                                    "." -> onDecimal()
                                    else -> onDigit(key.toInt())
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (key == "⌫") {
                            Icon(Icons.Default.Cancel, contentDescription = "Backspace", tint = Color(0xFF64748B), modifier = Modifier.size(26.dp))
                        } else {
                            Text(text = key, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = VaultNavy))
                        }
                    }
                }
            }
        }
    }
}