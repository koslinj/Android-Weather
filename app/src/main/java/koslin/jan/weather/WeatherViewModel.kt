package koslin.jan.weather

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import koslin.jan.weather.config.SharedPreferencesKeys
import koslin.jan.weather.data.LocationData
import koslin.jan.weather.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.abs

sealed interface WeatherUiState {
    data class Success(
        val time: List<String>,
        val temperature: List<Double>,
        val rain: List<Double>
    ) : WeatherUiState

    object Loading : WeatherUiState
    object Error : WeatherUiState
}

class WeatherViewModel(private val repository: Repository, application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<WeatherUiState>(WeatherUiState.Loading)
    val uiState: LiveData<WeatherUiState>
        get() = _uiState

    private val sharedPreferences = application.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private var locationData: LocationData = getLocationDataFromPreferences()

    private val _defaultCity = MutableLiveData<String>()
    val defaultCity: LiveData<String>
        get() = _defaultCity

    private fun getLocationDataFromPreferences(): LocationData {
        val defaultLatitude = sharedPreferences.getFloat(SharedPreferencesKeys.LATITUDE_KEY, 52.2298f).toDouble()
        val defaultLongitude = sharedPreferences.getFloat(SharedPreferencesKeys.LONGITUDE_KEY, 21.0118f).toDouble()
        val defaultCity = sharedPreferences.getString(SharedPreferencesKeys.DEFAULT_CITY_KEY, "DefaultCity")
        return LocationData(defaultLatitude, defaultLongitude, defaultCity!!)
    }

    fun getWeatherData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = WeatherUiState.Loading
                    }
                    // Perform network request here
                    val res = repository.getWeather(locationData.latitude, locationData.longitude)
                    val currentDateTime = Date()
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                    val currentHour = dateFormat.format(currentDateTime)

                    // Find the closest timestamp to the current time in the time array
                    val closestTimestamp = res.weatherData.time.minByOrNull {
                        abs(dateFormat.parse(it).time - currentDateTime.time)
                    }

                    val startIndex = res.weatherData.time.indexOf(closestTimestamp)

                    // Extract the next 24 elements
                    val endIndex = startIndex + 24

                    val slicedTime = res.weatherData.time.subList(startIndex, endIndex)
                    val slicedTemperature = res.weatherData.temperature.subList(startIndex, endIndex)
                    val slicedRain = res.weatherData.rain.subList(startIndex, endIndex)
                    withContext(Dispatchers.Main) {
                        _uiState.value = WeatherUiState.Success(slicedTime,slicedTemperature,slicedRain)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error
                Log.e("TEST", "Exception: ${e.message}", e)
            }
        }
    }

    init {
        getWeatherData()
        _defaultCity.value = locationData.cityName
    }

    // Function to update location data
    fun updateLocationData(locationData: LocationData, changeDefault: Boolean) {
        this.locationData = locationData
        // Save updated location data to SharedPreferences
        if(changeDefault){
            saveLocationDataToPreferences(locationData)
            _defaultCity.value = locationData.cityName
        }
    }

    private fun saveLocationDataToPreferences(locationData: LocationData) {
        val editor = sharedPreferences.edit()
        editor.putFloat(SharedPreferencesKeys.LATITUDE_KEY, locationData.latitude.toFloat())
        editor.putFloat(SharedPreferencesKeys.LONGITUDE_KEY, locationData.longitude.toFloat())
        editor.putString(SharedPreferencesKeys.DEFAULT_CITY_KEY, locationData.cityName)
        editor.apply()
    }

    fun getCurrentCity(): String {
        return locationData.cityName
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WeatherApplication)
                val repository = application.container.repository
                WeatherViewModel(repository = repository, application = application)
            }
        }
    }
}

