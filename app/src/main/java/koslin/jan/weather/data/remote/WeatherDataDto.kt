package koslin.jan.weather.data.remote

import com.squareup.moshi.Json

data class WeatherDataDto(
    val time: List<String>,
    @Json(name = "temperature_2m")
    val temperature: List<Double>,
    val rain: List<Double>,
    @Json(name = "wind_speed_10m")
    val windSpeed: List<Double>,
    @Json(name = "surface_pressure")
    val pressure: List<Double>,
    @Json(name = "cloud_cover")
    val cloudPercentage: List<Int>
)
