package koslin.jan.weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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

class WeatherViewModel(private val repository: Repository) : ViewModel() {

    private val _uiState = MutableLiveData<WeatherUiState>(WeatherUiState.Loading)
    val uiState: LiveData<WeatherUiState>
        get() = _uiState

    private var currentCity: String = "Warszawa"

    fun getWeatherData(lat: Double, long: Double) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Perform network request here
                    val res = repository.getWeather(lat, long)
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
        // You can provide default values for latitude, longitude, and city
        getWeatherData(52.2298, 21.0118)
    }

    // You can add a public function to update the city externally
    fun updateCurrentCity(city: String) {
        currentCity = city
    }

    // Add getter method for the current city
    fun getCurrentCity(): String {
        return currentCity
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WeatherApplication)
                val repository = application.container.repository
                WeatherViewModel(repository = repository)
            }
        }
    }
}

