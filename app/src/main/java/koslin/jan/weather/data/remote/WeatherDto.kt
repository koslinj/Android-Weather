package koslin.jan.weather.data.remote

import com.squareup.moshi.Json

data class WeatherDto(
    @Json(name = "hourly")
    val weatherData: WeatherDataDto
)
