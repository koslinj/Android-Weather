package koslin.jan.weather.fragments

import android.os.Bundle
import android.util.Log
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

                CoroutineScope(Dispatchers.IO).launch {
                    val timeLen = weatherInfo[0].time.length
                    val groupedWeatherInfo = mutableListOf<MutableList<SingleWeatherInfo>>()

                    var group = mutableListOf<SingleWeatherInfo>()

                    for (item in weatherInfo){
                        if(item.time.subSequence(timeLen-5, timeLen) == "10:00"){
                            group.add(SingleWeatherInfo(item.time, item.temperature, item.rain, cloudPercentage = item.cloudPercentage))
                        } else if(item.time.subSequence(timeLen-5, timeLen) == "15:00"){
                            group.add(SingleWeatherInfo(item.time, item.temperature, item.rain, cloudPercentage = item.cloudPercentage))
                        } else if(item.time.subSequence(timeLen-5, timeLen) == "20:00"){
                            group.add(SingleWeatherInfo(item.time, item.temperature, item.rain, cloudPercentage = item.cloudPercentage))
                            groupedWeatherInfo.add(group)
                            group = mutableListOf()
                        }
                    }
                    withContext(Dispatchers.Main) {
                        updateUI(groupedWeatherInfo, temperatureUnit)
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

    private fun updateUI(weatherInfo: List<List<SingleWeatherInfo>>, temperatureUnit: String) {
        adapter.updateData(weatherInfo, temperatureUnit)
    }

}