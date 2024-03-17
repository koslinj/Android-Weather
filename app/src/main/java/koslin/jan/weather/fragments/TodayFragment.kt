package koslin.jan.weather.fragments

import android.location.Geocoder
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.sidesheet.SideSheetDialog
import koslin.jan.weather.ModalBottomSheet
import koslin.jan.weather.R
import koslin.jan.weather.WeatherUiState
import koslin.jan.weather.WeatherViewModel
import koslin.jan.weather.data.LocationData
import koslin.jan.weather.data.SingleWeatherInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class TodayFragment : Fragment(R.layout.fragment_today) {

    private lateinit var todayMainTv: TextView
    private lateinit var cityNameEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var showButton: Button
    private lateinit var addToFavButton: Button
    private lateinit var loadingProgressBar: ProgressBar

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeatherAdapter
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayMainTv = view.findViewById(R.id.todayMainTv)
        cityNameEditText = view.findViewById(R.id.editTextCityName)
        searchButton = view.findViewById(R.id.buttonSearch)
        showButton = view.findViewById(R.id.showButton)
        addToFavButton = view.findViewById(R.id.addToFavButton)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)

        recyclerView = view.findViewById(R.id.weatherRecyclerView)
        adapter = WeatherAdapter(emptyList(), "", "")
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        weatherViewModel = ViewModelProvider(requireActivity(), WeatherViewModel.Factory)
            .get(WeatherViewModel::class.java)

        weatherViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            handleUiState(uiState)
        }

        searchButton.setOnClickListener {
            // Handle the search logic here
            val cityName = cityNameEditText.text.toString()
            // Call the method to handle geocoding and weather data fetching
            weatherViewModel.handleSearch(cityName, false)
        }

        showButton.setOnClickListener {
            showBottomSheet()
        }

        addToFavButton.setOnClickListener {
            weatherViewModel.addToFavorites()
        }
    }

    private fun showBottomSheet() {
        val modalBottomSheet = ModalBottomSheet()
        modalBottomSheet.show(childFragmentManager, ModalBottomSheet.TAG)
    }

    private fun handleUiState(uiState: WeatherUiState) {
        when (uiState) {
            is WeatherUiState.Success -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val weatherInfo = uiState.weatherInfo
                    val temperatureUnit = uiState.temperatureUnit
                    val windSpeedUnit = uiState.windSpeedUnit

                    val currentDateTime = Date()
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())

                    val closestTimestamp = weatherInfo.minByOrNull {
                        abs(dateFormat.parse(it.time).time - currentDateTime.time)
                    }?.time

                    val formattedWeatherInfo = closestTimestamp?.let { closestTime ->
                        val startIndex = weatherInfo.indexOfFirst { it.time == closestTime }
                        val endIndex = startIndex + 24

                        val slicedWeatherInfo = weatherInfo.subList(startIndex, endIndex)
                        slicedWeatherInfo.map { SingleWeatherInfo(it.time, it.temperature, it.rain, it.windSpeed) }
                    } ?: emptyList()

                    withContext(Dispatchers.Main) {
                        updateUI(formattedWeatherInfo, temperatureUnit, windSpeedUnit)
                        todayMainTv.text = weatherViewModel.getCurrentCity()
                        loadingProgressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
            }
            WeatherUiState.Loading -> {
                todayMainTv.text = getString(R.string.loading)
                loadingProgressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            WeatherUiState.Error -> {
                loadingProgressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
            }
        }
    }

    private fun updateUI(weatherInfo: List<SingleWeatherInfo>, temperatureUnit: String, windSpeedUnit: String) {
        adapter.updateData(weatherInfo, temperatureUnit, windSpeedUnit)
    }
}
