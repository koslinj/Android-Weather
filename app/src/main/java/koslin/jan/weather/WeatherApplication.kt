package koslin.jan.weather

import android.app.Application
import koslin.jan.weather.data.AppContainer
import koslin.jan.weather.data.DefaultAppContainer

class WeatherApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}