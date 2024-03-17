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

class TodayFragment : Fragment(R.layout.fragment_today) {

    private lateinit var todayMainTv: TextView
    private lateinit var cityNameEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var showButton: Button
    private lateinit var addToFavButton: ImageButton
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
        adapter = WeatherAdapter(emptyList(), emptyList(), emptyList(), "")
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
                val time = uiState.time
                val temperature = uiState.temperature
                val rain = uiState.rain
                val temperatureUnit = uiState.temperatureUnit

                // Now you can use this data to update your UI
                updateUI(time, temperature, rain, temperatureUnit)
                todayMainTv.text = weatherViewModel.getCurrentCity()
                loadingProgressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
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

    private fun updateUI(time: List<String>, temperature: List<Double>, rain: List<Double>, temperatureUnit: String) {
        adapter.updateData(time, temperature, rain, temperatureUnit)
    }
}
