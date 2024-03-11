package koslin.jan.weather.data

import koslin.jan.weather.data.remote.WeatherApi
import koslin.jan.weather.data.remote.WeatherDto

interface Repository {
    suspend fun getWeather(): WeatherDto
}

class NetworkRepository(private val api: WeatherApi) : Repository {
    override suspend fun getWeather(): WeatherDto {
        return api.getWeatherData(52.2298, 21.0118)
    }

}