package com.example.vault.repo

import com.example.vault.data.Transaction
import com.example.vault.data.TransactionDao
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    // Grabs the continuous stream of transactions from the database
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    fun getTotalAmountByType(type: String): Flow<Double?> {
        return transactionDao.getTotalAmountByType(type)
    }
}