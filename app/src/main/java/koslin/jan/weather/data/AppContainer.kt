package koslin.jan.weather.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import koslin.jan.weather.data.remote.WeatherApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

interface AppContainer {
    val repository: Repository
    val moshi: Moshi
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://api.open-meteo.com/"

    override val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(baseUrl)
        .build();

    private val retrofitService : WeatherApi by lazy {
        retrofit.create()
    }

    override val repository: Repository by lazy {
        NetworkRepository(retrofitService)
    }
}