package koslin.jan.weather.data

data class SingleWeatherInfo(
    val time: String, // Time of the forecast (e.g., "10:00", "15:00", "20:00")
    val temperature: Double, // Temperature for the hour
    val rain: Double
    // Add other weather information as needed (e.g., humidity, wind speed, etc.)
)