package com.example.vault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.vault.data.AppDatabase
import com.example.vault.repo.TransactionRepository
import com.example.vault.viewmodel.TransactionViewModel
import com.example.vault.viewmodel.TransactionViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { TransactionRepository(database.transactionDao()) }

    private val viewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VaultApp(viewModel = viewModel)
        }
    }
}
