package com.example.campushub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.campushub.data.MockData
import com.example.campushub.navigation.NavGraph
import com.example.campushub.navigation.Screen
import com.example.campushub.ui.theme.CampusHubTheme
import com.example.campushub.utils.NetworkUtils
import com.example.campushub.utils.PreferencesManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesManager.init(this)
        NetworkUtils.init(this)
        MockData.loadFromPreferences()
        enableEdgeToEdge()
        setContent {
            CampusHubTheme {
                val navController = rememberNavController()
                val startRoute = Screen.Login.route
                NavGraph(
                    navController = navController,
                    startDestination = startRoute
                )
            }
        }
    }
}
