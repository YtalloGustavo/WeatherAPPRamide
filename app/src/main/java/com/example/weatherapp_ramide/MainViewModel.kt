package com.example.weatherapp_ramide

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp_ramide.api.WeatherService
import com.example.weatherapp_ramide.db.fb.FBDatabase
import com.example.weatherapp_ramide.db.fb.FBCity
import com.example.weatherapp_ramide.db.fb.FBUser
import com.example.weatherapp_ramide.db.fb.toFBCity
import com.example.weatherapp_ramide.db.fb.toFBUser
import com.example.weatherapp_ramide.model.City
import com.example.weatherapp_ramide.model.User
import com.google.android.gms.maps.model.LatLng

class MainViewModel(
    private val db: FBDatabase,
    private val service: WeatherService
) : ViewModel(), FBDatabase.Listener {
    private val _cities = mutableStateListOf<City>()
    val cities
        get() = _cities.toList()

    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    init {
        db.setListener(this)
    }

    fun remove(city: City) {
        db.remove(city.toFBCity())
    }

    fun addCity(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                db.add(City(name = name, location = LatLng(lat, lng)).toFBCity())
            }
        }
    }

    fun addCity(location: LatLng) {
        service.getName(location.latitude, location.longitude) { name ->
            if (name != null) {
                db.add(City(name = name, location = location).toFBCity())
            }
        }
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        // TODO("Not yet implemented")
    }

    override fun onCityAdded(city: FBCity) {
        val newCity = city.toCity()
        if (_cities.none { it.name == newCity.name }) {
            _cities.add(newCity)
        }
    }

    override fun onCityUpdated(city: FBCity) {
        // TODO("Not yet implemented")
    }

    override fun onCityRemoved(city: FBCity) {
        _cities.removeAll { it.name == city.name }
    }
}

class MainViewModelFactory(
    private val db: FBDatabase,
    private val service: WeatherService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(db, service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
