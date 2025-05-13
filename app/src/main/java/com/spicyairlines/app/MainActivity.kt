package com.spicyairlines.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.spicyairlines.app.navigation.NavigationGraph
import com.spicyairlines.app.ui.viewmodel.SharedViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val navController = rememberNavController()

                val sharedViewModel: SharedViewModel = viewModel()

                // Llamamos al NavGraph y le pasamos el ViewModel compartido
                NavigationGraph(navController = navController, sharedViewModel = sharedViewModel)
            }
        }
    }
}




