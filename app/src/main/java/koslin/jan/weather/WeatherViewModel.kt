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

sealed interface WeatherUiState {
    data class Success(
        val hourlyTime: List<String>,
        val hourlyTemperature: List<Double>,
        val hourlyRainfall: List<Double>
    ) : WeatherUiState

    object Loading : WeatherUiState
    object Error : WeatherUiState
}

class WeatherViewModel(private val repository: Repository) : ViewModel() {

    private val _uiState = MutableLiveData<WeatherUiState>(WeatherUiState.Loading)
    val uiState: LiveData<WeatherUiState>
        get() = _uiState

    fun getWeatherData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Perform network request here
                    val res = repository.getWeather()
                    withContext(Dispatchers.Main) {
                        // Update UI on the main thread
                        _uiState.value = WeatherUiState.Success(
                            res.weatherData.time,
                            res.weatherData.temperature,
                            res.weatherData.rain
                        )
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
