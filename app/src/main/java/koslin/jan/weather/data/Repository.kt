package koslin.jan.weather.data

import koslin.jan.weather.data.remote.WeatherApi
import koslin.jan.weather.data.remote.WeatherDto

interface Repository {
    suspend fun getWeather(lat: Double, long: Double, temperatureUnit: String): WeatherDto
}

class NetworkRepository(private val api: WeatherApi) : Repository {
    override suspend fun getWeather(lat: Double, long: Double, temperatureUnit: String): WeatherDto {
        return if (temperatureUnit == "fahrenheit") {
            api.getWeatherData(lat, long, "fahrenheit")
        } else {
            api.getWeatherData(lat, long)
        }
    }

}