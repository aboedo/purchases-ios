package com.revenuecat.samplecat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.revenuecat.samplecat.ui.ErrorView
import com.revenuecat.samplecat.ui.MainViewModel
import com.revenuecat.samplecat.ui.OfferingsView

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleCatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val error by viewModel.error.collectAsState()
    val currentError = error

    if (currentError != null) {
        ErrorView(errorMessage = currentError, onRetry = { viewModel.loadInitialData() })
    } else {
        OfferingsView(viewModel = viewModel)
    }
}

@Composable
fun SampleCatTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}
