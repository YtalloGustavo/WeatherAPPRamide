package com.example.weatherapp_ramide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp_ramide.ui.nav.BottomNavBar
import com.example.weatherapp_ramide.ui.nav.BottomNavItem
import com.example.weatherapp_ramide.ui.nav.MainNavHost
import com.example.weatherapp_ramide.ui.theme.WeatherAPPRamideTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(onExit = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onExit: () -> Unit, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.HomeButton,
        BottomNavItem.ListButton,
        BottomNavItem.MapButton
    )

    WeatherAPPRamideTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Bem-vindo/a!") },
                    actions = {
                        IconButton(onClick = onExit) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Sair"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBar(navController = navController, items = items)
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { }) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar")
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                MainNavHost(navController = navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(onExit = {})
}
