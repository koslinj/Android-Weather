package koslin.jan.weather.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import koslin.jan.weather.R
import koslin.jan.weather.WeatherUiState
import koslin.jan.weather.WeatherViewModel
import koslin.jan.weather.data.SingleWeatherInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class WeekFragment : Fragment(R.layout.fragment_week) {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeeklyWeatherAdapter

    private lateinit var loadingProgressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)

        recyclerView = view.findViewById(R.id.weatherRecyclerView)
        adapter = WeeklyWeatherAdapter(emptyList(), "")
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        weatherViewModel = ViewModelProvider(requireActivity(), WeatherViewModel.Factory)
            .get(WeatherViewModel::class.java)
        weatherViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            handleUiState(uiState)
        }
    }

    private fun handleUiState(uiState: WeatherUiState) {
        when (uiState) {
            is WeatherUiState.Success -> {
                val weatherInfo = uiState.weatherInfo
                val temperatureUnit = uiState.temperatureUnit

                CoroutineScope(Dispatchers.Default).launch {
                    val filteredWeatherInfo = mutableListOf<SingleWeatherInfo>()

                    val currentDateTime = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())

                    repeat(7) { dayIndex ->
                        val currentDay = currentDateTime.clone() as Calendar
                        currentDay.add(Calendar.DAY_OF_YEAR, dayIndex)

                        val dayWeatherInfo = weatherInfo.filter { weather ->
                            val dateTime = dateFormat.parse(weather.time)
                            val weatherDateTime = Calendar.getInstance()
                            weatherDateTime.time = dateTime
                            weatherDateTime.get(Calendar.DAY_OF_YEAR) == currentDay.get(Calendar.DAY_OF_YEAR) &&
                                    (weatherDateTime.get(Calendar.HOUR_OF_DAY) == 10 ||
                                            weatherDateTime.get(Calendar.HOUR_OF_DAY) == 15 ||
                                            weatherDateTime.get(Calendar.HOUR_OF_DAY) == 20)
                        }

                        filteredWeatherInfo.addAll(dayWeatherInfo)
                    }

                    withContext(Dispatchers.Main) {
                        updateUI(filteredWeatherInfo, temperatureUnit)
                        loadingProgressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }

            }

            WeatherUiState.Loading -> {
                loadingProgressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }

            WeatherUiState.Error -> {
                loadingProgressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
            }
        }
    }

    private fun updateUI(weatherInfo: List<SingleWeatherInfo>, temperatureUnit: String) {
        adapter.updateData(weatherInfo, temperatureUnit)
    }

}