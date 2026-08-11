package com.example.weatherapp_ramide

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp_ramide.api.WeatherService
import com.example.weatherapp_ramide.api.toForecast
import com.example.weatherapp_ramide.api.toWeather
import com.example.weatherapp_ramide.db.fb.FBDatabase
import com.example.weatherapp_ramide.db.local.LocalDatabase
import com.example.weatherapp_ramide.model.City
import com.example.weatherapp_ramide.model.Forecast
import com.example.weatherapp_ramide.model.Weather
import com.example.weatherapp_ramide.monitor.ForecastMonitor
import com.example.weatherapp_ramide.repo.Repository
import com.example.weatherapp_ramide.ui.nav.Route
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val repo: Repository,
    private val service: WeatherService,
    private val monitor: ForecastMonitor
) : ViewModel() {

    private var _city = mutableStateOf<String?>(null)
    var city: String?
        get() = _city.value
        set(tmp) { _city.value = tmp }

    private var _page = mutableStateOf<Route>(Route.Home)
    var page: Route
        get() = _page.value
        set(tmp) { _page.value = tmp }

    override fun onCleared() {
        repo.close()
        monitor.cancelAll()
        super.onCleared()
    }

    private val _cities: kotlinx.coroutines.flow.Flow<Map<String, City>> = repo.cities.map {
        cityList -> cityList.associateBy { it.name }
    }
    val cities = _cities.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    private val _weather = MutableStateFlow<Map<String, Weather>>(emptyMap())
    val weather = _weather.asStateFlow()

    private val _forecast = MutableStateFlow<Map<String, List<Forecast>?>>(emptyMap())
    val forecast = _forecast.asStateFlow()

    val user = repo.user.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun remove(city: City) {
        repo.remove(city)
        monitor.cancelCity(city)
    }

    fun update(city: City) {
        repo.update(city)
        monitor.updateCity(city)
    }

    fun addCity(name: String) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { service.getLocation(name) }.getOrNull()?.let { location ->
            val city = City(name = name, location = location)
            repo.add(city)
            withContext(Dispatchers.Main) { monitor.updateCity(city) }
        }
    }

    fun addCity(location: LatLng) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { service.getName(location.latitude, location.longitude) }
            .getOrNull()?.let { name ->
                val city = City(name = name ?: "Local", location = location)
                repo.add(city)
                withContext(Dispatchers.Main) { monitor.updateCity(city) }
            }
    }

private val loading = mutableSetOf<String>()

    fun loadWeather(name: String) {
        val current = _weather.value[name]
        if (current != null && current != Weather.ERROR) return
        if (!loading.add("w:$name")) return

        viewModelScope.launch(Dispatchers.Main) {
            try {
                runCatching {
                    service.getWeather(name)?.toWeather()
                }.onSuccess { weather ->
                    _weather.update { curr -> curr + (name to (weather ?: Weather.ERROR)) }
                }.onFailure {
                    _weather.update { curr -> curr + (name to Weather.ERROR) }
                }
            } finally {
                loading.remove("w:$name")
            }
        }
    }

    fun loadForecast(name: String) {
        val current = _forecast.value[name]
        if (current != null && current.isNotEmpty()) return
        if (!loading.add("f:$name")) return

        viewModelScope.launch(Dispatchers.Main) {
            try {
                runCatching {
                    service.getForecast(name)?.toForecast()
                }.onSuccess { forecast ->
                    _forecast.update { curr -> curr + (name to forecast) }
                }.onFailure {
                    _forecast.update { curr -> curr + (name to emptyList()) }
                }
            } finally {
                loading.remove("f:$name")
            }
        }
    }

    fun loadBitmap(name: String) {
        val weather = _weather.value[name]
        if (weather == null || weather == Weather.LOADING || weather == Weather.ERROR ||
            weather.bitmap != null
        ) return

        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = service.getBitmap(weather.imgUrl)
            _weather.update { current ->
                current[name]?.let { w ->
                    current + (name to w.copy(bitmap = bitmap))
                } ?: current
            }
        }
    }
}

class MainViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val uid = Firebase.auth.currentUser?.uid ?: "anon"
            val fbDB = FBDatabase()
            val localDB = LocalDatabase(context, "weatherdb_$uid")
            val repo = Repository(fbDB, localDB)
            val service = WeatherService(context)
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repo, service, ForecastMonitor(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}