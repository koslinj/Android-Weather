package koslin.jan.weather.data

data class SingleWeatherInfo(
    val time: String,
    val temperature: Double,
    val rain: Double,
    val windSpeed: Double? = null,
    val pressure: Double? = null,
    val cloudPercentage: Int? = null
)