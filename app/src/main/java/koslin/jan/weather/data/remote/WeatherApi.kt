package koslin.jan.weather.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("v1/forecast?hourly=temperature_2m,rain,wind_speed_10m,surface_pressure,cloud_cover")
    suspend fun getWeatherData(
        @Query("latitude") lat: Double,
        @Query("longitude") long: Double,
        @Query("temperature_unit") temperatureUnit: String? = null,
        @Query("wind_speed_unit") windSpeedUnit: String? = null
    ): WeatherDto
}