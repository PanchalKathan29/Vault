package com.example.vault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vault.data.Transaction
import com.example.vault.repo.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    // 1. Expose the list of transactions to the UI
    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList() // The UI will show an empty list while loading
        )

    // 2. Calculate Total Income for the Dashboard
    val totalIncome: StateFlow<Double> = repository.getTotalAmountByType("Income")
        .map { it ?: 0.0 } // If the database returns null (no entries), default to 0.0
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // 3. Calculate Total Expense for the Dashboard
    val totalExpense: StateFlow<Double> = repository.getTotalAmountByType("Expense")
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // 4. Function for the UI to call when a user clicks "Save" on a new transaction
    fun addTransaction(amount: Double, type: String, category: String, dateMillis: Long, notes: String = "") {
        viewModelScope.launch {
            val newTransaction = Transaction(
                amount = amount,
                type = type,
                category = category,
                dateMillis = dateMillis,
                notes = notes
            )
            repository.insertTransaction(newTransaction)
        }
    }

    // 5. Function to delete a transaction (e.g., if the user swipes it away)
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}

// Because our ViewModel requires a Repository in its constructor,
// we need a Factory to tell Android how to build it.
class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}