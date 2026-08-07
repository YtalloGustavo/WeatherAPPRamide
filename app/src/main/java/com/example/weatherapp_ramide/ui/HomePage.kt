package com.example.weatherapp_ramide.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.weatherapp_ramide.MainViewModel
import com.example.weatherapp_ramide.R
import com.example.weatherapp_ramide.model.Weather
import com.example.weatherapp_ramide.model.Forecast
import java.text.DecimalFormat

@Composable
fun HomePage(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Column {
        if (viewModel.city == null) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Blue)
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "Selecione uma cidade!",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp
                )
            }
        } else {
            val cities = viewModel.cities.collectAsStateWithLifecycle(emptyMap()).value
            val cityName = viewModel.city!!
            val city = cities[cityName]
            val weather = viewModel.weather.collectAsStateWithLifecycle(emptyMap())
                .value[cityName]
            val forecasts = viewModel.forecast.collectAsStateWithLifecycle(emptyMap())
                .value[cityName]

            LaunchedEffect(cityName) {
                viewModel.loadForecast(cityName)
            }
            LaunchedEffect(cityName) {
                viewModel.loadWeather(cityName)
            }

            Row {
                AsyncImage(
                    model = weather?.imgUrl,
                    modifier = modifier.size(140.dp),
                    error = painterResource(id = R.drawable.loading),
                    contentDescription = "Imagem"
                )
                Column {
                    Spacer(modifier = modifier.size(12.dp))
                    val icon = if (city?.isMonitored == true)
                        R.drawable.ic_notifications_filled
                    else R.drawable.ic_notifications_outlined
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = cityName,
                            fontSize = 28.sp
                        )
                        if (city != null) {
                            Spacer(modifier = Modifier.size(12.dp))
                            Icon(
                                painter = painterResource(icon),
                                contentDescription = "Monitorada?",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        viewModel.update(
                                            city.copy(isMonitored = !city.isMonitored)
                                        )
                                    }
                            )
                        }
                    }
                    if (weather != null) {
                        Spacer(modifier = modifier.size(12.dp))
                        Text(
                            text = weather.desc,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = modifier.size(12.dp))
                        Text(
                            text = "Temp: " + weather.temp + "℃",
                            fontSize = 22.sp
                        )
                    }
                }
            }

            forecasts?.let { forecastList ->
                LazyColumn {
                    items(items = forecastList, key = { it.date }) { forecast ->
                        ForecastItem(forecast, onClick = { })
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItem(
    forecast: Forecast,
    modifier: Modifier = Modifier,
    onClick: (Forecast) -> Unit
) {
    val format = DecimalFormat("#.0")
    val tempMin = format.format(forecast.tempMin)
    val tempMax = format.format(forecast.tempMax)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(onClick = { onClick(forecast) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = forecast.imgUrl,
            modifier = modifier.size(70.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = modifier.size(16.dp))
        Column {
            Text(text = forecast.weather, fontSize = 24.sp)
            Row {
                Text(text = forecast.date, fontSize = 20.sp)
                Spacer(modifier = modifier.size(12.dp))
                Text(text = "Min: $tempMin℃", fontSize = 16.sp)
                Spacer(modifier = modifier.size(12.dp))
                Text(text = "Max: $tempMax℃", fontSize = 16.sp)
            }
        }
    }
}