package com.example.weatherapp_ramide.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp_ramide.MainViewModel
import com.example.weatherapp_ramide.ui.HomePage
import com.example.weatherapp_ramide.ui.ListPage
import com.example.weatherapp_ramide.ui.MapPage

@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(viewModel = viewModel, modifier = modifier) }
        composable<Route.List> { ListPage(viewModel = viewModel, modifier = modifier) }
        composable<Route.Map> { MapPage(viewModel = viewModel, modifier = modifier) }
    }
}
