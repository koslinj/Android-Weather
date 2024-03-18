package koslin.jan.weather.data


data class WeatherInfoWithUnits(
    val weatherInfo: List<SingleWeatherInfo>,
    val temperatureUnit: String,
    val windSpeedUnit: String
)
