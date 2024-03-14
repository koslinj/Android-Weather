package koslin.jan.weather.fragments

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import koslin.jan.weather.R
import koslin.jan.weather.WeatherUiState
import koslin.jan.weather.WeatherViewModel

class TodayFragment : Fragment(R.layout.fragment_today) {

    private lateinit var todayMainTv: TextView
    private lateinit var cityNameEditText: EditText
    private lateinit var searchButton: Button

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeatherAdapter
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayMainTv = view.findViewById(R.id.todayMainTv)
        cityNameEditText = view.findViewById(R.id.editTextCityName)
        searchButton = view.findViewById(R.id.buttonSearch)

        recyclerView = view.findViewById(R.id.weatherRecyclerView)
        adapter = WeatherAdapter(emptyList(), emptyList(), emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        weatherViewModel = ViewModelProvider(requireActivity(), WeatherViewModel.Factory)
            .get(WeatherViewModel::class.java)

        weatherViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            handleUiState(uiState)

            if (uiState is WeatherUiState.Success) {
                todayMainTv.text = weatherViewModel.getCurrentCity()
            }
        }

        searchButton.setOnClickListener {
            // Handle the search logic here
            val cityName = cityNameEditText.text.toString()
            // Call the method to handle geocoding and weather data fetching
            handleSearch(cityName)
        }

    }

    private fun handleSearch(cityName: String) {
        // Geocoding logic
        val geocoder = Geocoder(requireContext())
        val addresses = geocoder.getFromLocationName(cityName, 1)

        if (addresses != null && addresses.isNotEmpty()) {
            val latitude = addresses[0].latitude
            val longitude = addresses[0].longitude
            val city = addresses[0].locality
            weatherViewModel.updateCurrentCity(city)
            // Call the API with the obtained latitude and longitude
            weatherViewModel.getWeatherData(latitude, longitude)
        } else {
            // Handle no results or error
        }
    }

    private fun handleUiState(uiState: WeatherUiState) {
        when (uiState) {
            is WeatherUiState.Success -> {
                val time = uiState.time
                val temperature = uiState.temperature
                val rain = uiState.rain

                // Now you can use this data to update your UI
                updateUI(time, temperature, rain)
            }
            WeatherUiState.Loading -> {

            }
            WeatherUiState.Error -> {

            }
        }
    }

    private fun updateUI(time: List<String>, temperature: List<Double>, rain: List<Double>) {
        adapter.updateData(time, temperature, rain)
    }
}
