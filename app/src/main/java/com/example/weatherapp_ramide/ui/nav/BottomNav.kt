package com.example.weatherapp_ramide.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weatherapp_ramide.MainViewModel
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object List : Route

    @Serializable
    data object Map : Route
}

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: Route
) {
    data object HomeButton : BottomNavItem("Inicio", Icons.Default.Home, Route.Home)
    data object ListButton : BottomNavItem("Favoritos", Icons.Default.Favorite, Route.List)
    data object MapButton : BottomNavItem("Mapa", Icons.Default.LocationOn, Route.Map)
}

@Composable
fun BottomNavBar(
    viewModel: MainViewModel,
    navController: NavHostController,
    items: List<BottomNavItem>
) {
    NavigationBar(contentColor = Color.Black) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = { Text(text = item.title, fontSize = 12.sp) },
                alwaysShowLabel = true,
                selected = viewModel.page == item.route,
                onClick = {
                    viewModel.page = item.route
                }
            )
        }
    }
}
