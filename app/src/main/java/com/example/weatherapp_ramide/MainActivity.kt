package com.example.weatherapp_ramide

import android.os.Bundle
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp_ramide.ui.CityDialog
import com.example.weatherapp_ramide.ui.nav.BottomNavBar
import com.example.weatherapp_ramide.ui.nav.BottomNavItem
import com.example.weatherapp_ramide.ui.nav.MainNavHost
import com.example.weatherapp_ramide.ui.nav.Route
import com.example.weatherapp_ramide.ui.theme.WeatherAPPRamideTheme
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LEGACY, null)
        } catch (exception: RuntimeException) {
            Log.e("MainActivity", "Falha ao inicializar o Google Maps.", exception)
        }
        setContent {
            val uid = Firebase.auth.currentUser?.uid ?: run {
                Firebase.auth.signOut()
                startActivity(
                    Intent(this@MainActivity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                return@setContent
            }
            val fbDB = remember { com.example.weatherapp_ramide.db.fb.FBDatabase() }
            val localDB = remember {
                com.example.weatherapp_ramide.db.local.LocalDatabase(
                    this@MainActivity, "weatherdb_$uid"
                )
            }
            val repo = remember {
                com.example.weatherapp_ramide.repo.Repository(fbDB, localDB)
            }
            val weatherService = remember {
                com.example.weatherapp_ramide.api.WeatherService(this@MainActivity)
            }
            val monitor = remember {
                com.example.weatherapp_ramide.monitor.ForecastMonitor(this@MainActivity)
            }
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(repo, weatherService, monitor)
            )
            DisposableEffect(Unit) {
                val listener = androidx.core.util.Consumer<android.content.Intent> { intent ->
                    viewModel.city = intent.getStringExtra("city")
                    viewModel.page = Route.Home
                }
                this@MainActivity.addOnNewIntentListener(listener)
                onDispose { this@MainActivity.removeOnNewIntentListener(listener) }
            }
            MainScreen(
                viewModel = viewModel,
                onExit = {
                    Firebase.auth.signOut()
                    startActivity(
                        Intent(this@MainActivity, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var showDialog by remember { mutableStateOf(false) }
    val showAddButton = viewModel.page == Route.List
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )
    val context = androidx.compose.ui.platform.LocalContext.current
    val user = viewModel.user.collectAsStateWithLifecycle(null).value
    val items = listOf(
        BottomNavItem.HomeButton,
        BottomNavItem.ListButton,
        BottomNavItem.MapButton
    )

    WeatherAPPRamideTheme {
        LaunchedEffect(Unit) {
            val hasFineLocationPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!hasFineLocationPermission) {
                launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }

            val hasNotifPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!hasNotifPermission) {
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (showDialog) {
            CityDialog(
                onDismiss = { showDialog = false },
                onConfirm = { city ->
                    if (city.isNotBlank()) {
                        viewModel.addCity(city)
                    }
                    showDialog = false
                }
            )
        }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        val name = user?.name ?: "[carregando...]"
                        Text("Bem-vindo/a! $name")
                    },
                    actions = {
                        IconButton(onClick = onExit) {
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(
                                    com.example.weatherapp_ramide.R.drawable.ic_exit_to_app
                                ),
                                contentDescription = "Sair"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBar(viewModel = viewModel, navController = navController, items = items)
            },
            floatingActionButton = {
                if (showAddButton) {
                    FloatingActionButton(onClick = { showDialog = true }) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(
                                com.example.weatherapp_ramide.R.drawable.ic_add
                            ),
                            contentDescription = "Adicionar"
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                MainNavHost(
                    navController = navController,
                    viewModel = viewModel
                )
            }
            LaunchedEffect(viewModel.page) {
                navController.navigate(viewModel.page) {
                    navController.graph.startDestinationRoute?.let {
                        popUpTo(it) {
                            saveState = true
                        }
                        restoreState = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    MainScreen(
        viewModel = MainViewModel(
            com.example.weatherapp_ramide.repo.Repository(
                com.example.weatherapp_ramide.db.fb.FBDatabase(),
                com.example.weatherapp_ramide.db.local.LocalDatabase(context, "preview")
            ),
            com.example.weatherapp_ramide.api.WeatherService(context),
            com.example.weatherapp_ramide.monitor.ForecastMonitor(context)
        ),
        onExit = {}
    )
}
