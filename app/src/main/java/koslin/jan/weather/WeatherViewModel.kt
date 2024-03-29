package koslin.jan.weather

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.preference.PreferenceManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import koslin.jan.weather.config.Keys
import koslin.jan.weather.config.ToastType
import koslin.jan.weather.data.LocationData
import koslin.jan.weather.data.Repository
import koslin.jan.weather.data.SingleWeatherInfo
import koslin.jan.weather.data.WeatherInfoWithUnits
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

sealed interface WeatherUiState {
    data class Success(
        val weatherInfo: List<SingleWeatherInfo>,
        val temperatureUnit: String,
        val windSpeedUnit: String
    ) : WeatherUiState

    object Loading : WeatherUiState
    object Error : WeatherUiState
}

class WeatherViewModel(
    private val repository: Repository,
    moshi: Moshi,
    private val application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<WeatherUiState>(WeatherUiState.Loading)
    val uiState: LiveData<WeatherUiState>
        get() = _uiState

    private val listType = Types.newParameterizedType(List::class.java, SingleWeatherInfo::class.java)
    private val weatherInfoAdapter: JsonAdapter<List<SingleWeatherInfo>> = moshi.adapter(listType)
    private val jsonAdapter: JsonAdapter<WeatherInfoWithUnits> = moshi.adapter(
        WeatherInfoWithUnits::class.java)

    private val geocoder = Geocoder(application)
    private val connectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    private val customPreferences =
        application.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    private var locationData: LocationData = getLocationDataFromPreferences()
    private var temperatureUnit: String = getTemperatureUnitPreference()
    private var windSpeedUnit: String = getWindSpeedUnitPreference()

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
        return defaultPreferences.getString(Keys.TEMPERATURE_UNIT_KEY, "celsius") ?: "celsius"
    }

    private fun getWindSpeedUnitPreference(): String {
        return defaultPreferences.getString(Keys.WIND_UNIT_KEY, "kmh") ?: "kmh"
    }

    fun handleSearch(cityName: String, changeDefault: Boolean) {
        if (isNetworkAvailable()) {
            val addresses = geocoder.getFromLocationName(cityName, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val latitude = addresses[0].latitude
                val longitude = addresses[0].longitude
                val city = addresses[0].locality
                updateLocationData(LocationData(latitude, longitude, city), changeDefault)
                getWeatherData()
            } else {
                // Handle no results or error
            }
        } else {
            getWeatherFromFile(cityName)
            locationData.cityName = cityName
        }

    }

    private fun saveWeatherDataToFile(weatherInfo: WeatherInfoWithUnits, cityName: String) {
        try {
            val json = jsonAdapter.toJson(weatherInfo)
            val fileName = "$cityName.json"
            application.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        } catch (e: IOException) {
            Log.e("WeatherViewModel", "Error saving weather data to file: ${e.message}", e)
        }
    }

    private fun loadWeatherDataFromFile(cityName: String): WeatherInfoWithUnits? {
        return try {
            val fileName = "$cityName.json"
            val file = File(application.filesDir, fileName)
            if (file.exists()) {
                val json = file.readText()
                jsonAdapter.fromJson(json)
            } else {
                null
            }
        } catch (e: IOException) {
            Log.e("WeatherViewModel", "Error loading weather data from file: ${e.message}", e)
            null
        }
    }

    fun getWeatherFromFile(fileName: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = WeatherUiState.Loading
                    }
                    val weatherInfoFromFile = loadWeatherDataFromFile(fileName)
                    if (weatherInfoFromFile != null) {
                        withContext(Dispatchers.Main) {
                            _uiState.value = WeatherUiState.Success(
                                weatherInfoFromFile.weatherInfo,
                                weatherInfoFromFile.temperatureUnit,
                                weatherInfoFromFile.windSpeedUnit
                            )
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            _uiState.value = WeatherUiState.Error
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error
                Log.e("TEST", "Exception: ${e.message}", e)
            }
        }
    }

    fun getWeatherData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (isNetworkAvailable()) {
                        withContext(Dispatchers.Main) {
                            _uiState.value = WeatherUiState.Loading
                        }
                        val res = repository.getWeather(
                            locationData.latitude,
                            locationData.longitude,
                            temperatureUnit,
                            windSpeedUnit
                        )
                        val weatherInfo = List(res.weatherData.time.size) { index ->
                            SingleWeatherInfo(
                                res.weatherData.time[index],
                                res.weatherData.temperature[index],
                                res.weatherData.rain[index],
                                res.weatherData.windSpeed[index],
                                res.weatherData.pressure[index],
                                res.weatherData.cloudPercentage[index]
                            )
                        }
                        withContext(Dispatchers.Main) {
                            _uiState.value =
                                WeatherUiState.Success(weatherInfo, temperatureUnit, windSpeedUnit)
                        }
                        saveWeatherDataToFile(WeatherInfoWithUnits(weatherInfo, temperatureUnit, windSpeedUnit), locationData.cityName)
                    } else {
                        withContext(Dispatchers.Main) {
                            _uiState.value = WeatherUiState.Loading
                        }
                        val weatherInfoFromFile = loadWeatherDataFromFile(locationData.cityName)
                        if (weatherInfoFromFile != null) {
                            withContext(Dispatchers.Main) {
                                _uiState.value = WeatherUiState.Success(
                                    weatherInfoFromFile.weatherInfo,
                                    weatherInfoFromFile.temperatureUnit,
                                    weatherInfoFromFile.windSpeedUnit
                                )
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                _uiState.value = WeatherUiState.Error
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error
                Log.e("TEST", "Exception: ${e.message}", e)
            }
        }
    }

    fun isNetworkAvailable(): Boolean {
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun showCustomToast(context: Context, messageId: Int, type: ToastType) {
        val message = context.getString(messageId)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.custom_toast_layout, null)

        val textView = layout.findViewById<TextView>(R.id.toast_message)
        val imageView = layout.findViewById<ImageView>(R.id.toast_icon)
        val container = layout.findViewById<LinearLayout>(R.id.custom_toast_container)
        textView.text = message

        val toast = Toast(context)
        when(type){
            ToastType.INTERNET -> {
                toast.duration = Toast.LENGTH_LONG
            }
            ToastType.DEFAULT_CITY -> {
                container.setBackgroundResource(R.drawable.other_toast_background)
                imageView.setImageResource(R.drawable.notification_icon)
                toast.duration = Toast.LENGTH_SHORT
            }
            ToastType.FAVOURITE -> {
                container.setBackgroundResource(R.drawable.other_toast_background)
                imageView.setImageResource(R.drawable.heart_icon)
                toast.duration = Toast.LENGTH_SHORT
            }
        }

        toast.view = layout
        toast.show()
    }

    init {
        getWeatherData()
        if(!isNetworkAvailable()){
            showCustomToast(application, R.string.no_internet_initial, ToastType.INTERNET)
        }
        _defaultCity.value = locationData.cityName
    }

    fun addToFavorites() {
        val currentCity = getCurrentCity()
        val favoritesSet = customPreferences.getStringSet(Keys.FAVOURITE_CITIES_KEY, mutableSetOf())
            ?.toMutableSet() ?: mutableSetOf()
        favoritesSet.add(currentCity)
        customPreferences.edit().putStringSet(Keys.FAVOURITE_CITIES_KEY, favoritesSet).apply()
    }

    fun refresh() {
        if(isNetworkAvailable()){
            getWeatherData()
        }
        else{
            showCustomToast(application, R.string.no_internet_refresh, ToastType.INTERNET)
        }
    }

    // Function to update location data
    fun updateLocationData(locationData: LocationData, changeDefault: Boolean) {
        this.locationData = locationData
        // Save updated location data to SharedPreferences
        if (changeDefault) {
            saveLocationDataToPreferences(locationData)
            _defaultCity.value = locationData.cityName
        }
    }

    fun updateTemperatureUnit() {
        this.temperatureUnit = getTemperatureUnitPreference()
    }

    fun updateWindSpeedUnit() {
        this.windSpeedUnit = getWindSpeedUnitPreference()
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
                val moshi = application.container.moshi
                WeatherViewModel(repository = repository, moshi = moshi, application = application)
            }
        }
    }
}

