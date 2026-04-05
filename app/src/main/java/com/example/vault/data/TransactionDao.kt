package com.example.vault.data

import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface TransactionDao {

    // suspend means these functions must be called from a Coroutine (background thread)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    // Flow means the database will automatically emit a new list
    // to your UI every time a transaction is added, updated, or deleted!
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    // A handy query for your Dashboard to calculate total income or expenses
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    fun getTotalAmountByType(type: String): Flow<Double?>
}