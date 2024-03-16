package koslin.jan.weather

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.preference.PreferenceManager
import koslin.jan.weather.config.Keys
import koslin.jan.weather.data.LocationData
import koslin.jan.weather.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

sealed interface WeatherUiState {
    data class Success(
        val time: List<String>,
        val temperature: List<Double>,
        val rain: List<Double>,
        val temperatureUnit: String
    ) : WeatherUiState

    object Loading : WeatherUiState
    object Error : WeatherUiState
}

class WeatherViewModel(private val repository: Repository, application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<WeatherUiState>(WeatherUiState.Loading)
    val uiState: LiveData<WeatherUiState>
        get() = _uiState

    private val customPreferences = application.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    private var locationData: LocationData = getLocationDataFromPreferences()
    private var temperatureUnit: String = getTemperatureUnitPreference()

    private val _defaultCity = MutableLiveData<String>()
    val defaultCity: LiveData<String>
        get() = _defaultCity

    private fun getLocationDataFromPreferences(): LocationData {
        val defaultLatitude = customPreferences.getFloat(Keys.LATITUDE_KEY, 52.2298f).toDouble()
        val defaultLongitude = customPreferences.getFloat(Keys.LONGITUDE_KEY, 21.0118f).toDouble()
        val defaultCity = customPreferences.getString(Keys.DEFAULT_CITY_KEY, "Warszawa")
        return LocationData(defaultLatitude, defaultLongitude, defaultCity!!)
    }

    private fun getTemperatureUnitPreference(): String {
        return defaultPreferences.getString(Keys.TEMPERATURE_UNIT_KEY, "celsiusDefault") ?: "celsius"
    }

    fun getWeatherData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = WeatherUiState.Loading
                    }
                    // Perform network request here
                    val res = repository.getWeather(locationData.latitude, locationData.longitude, temperatureUnit)
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
                        _uiState.value = WeatherUiState.Success(slicedTime,slicedTemperature,slicedRain, temperatureUnit)
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

    fun updateTemperatureUnit() {
        this.temperatureUnit = getTemperatureUnitPreference()
    }

    private fun saveLocationDataToPreferences(locationData: LocationData) {
        val editor = customPreferences.edit()
        editor.putFloat(Keys.LATITUDE_KEY, locationData.latitude.toFloat())
        editor.putFloat(Keys.LONGITUDE_KEY, locationData.longitude.toFloat())
        editor.putString(Keys.DEFAULT_CITY_KEY, locationData.cityName)
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

