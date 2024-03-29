package koslin.jan.weather.data

import koslin.jan.weather.data.remote.WeatherApi
import koslin.jan.weather.data.remote.WeatherDto

interface Repository {
    suspend fun getWeather(lat: Double, long: Double, temperatureUnit: String, windSpeedUnit: String): WeatherDto
}

class NetworkRepository(private val api: WeatherApi) : Repository {
    override suspend fun getWeather(lat: Double, long: Double, temperatureUnit: String, windSpeedUnit: String): WeatherDto {
        return api.getWeatherData(lat, long, temperatureUnit, windSpeedUnit)
    }

}