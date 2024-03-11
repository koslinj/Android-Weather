package koslin.jan.weather.data.remote

import com.squareup.moshi.Json

data class WeatherDataDto(
    val time: List<String>,
    @Json(name = "temperature_2m")
    val temperature: List<Double>,
    val rain: List<Double>
)
