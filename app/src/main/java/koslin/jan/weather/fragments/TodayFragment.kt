package koslin.jan.weather.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import koslin.jan.weather.R
import koslin.jan.weather.WeatherUiState
import koslin.jan.weather.WeatherViewModel

class TodayFragment : Fragment(R.layout.fragment_today) {

    private lateinit var todayMainTv: TextView
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the TextView
        todayMainTv = view.findViewById(R.id.todayMainTv)

        // Obtain a reference to the ViewModel
        weatherViewModel = ViewModelProvider(requireActivity(), WeatherViewModel.Factory)
            .get(WeatherViewModel::class.java)

        // Observe the LiveData for UI updates
        weatherViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            handleUiState(uiState)
        }
    }

    private fun handleUiState(uiState: WeatherUiState) {
        when (uiState) {
            is WeatherUiState.Success -> {
                todayMainTv.text = uiState.hourlyTime[0]
            }
            WeatherUiState.Loading -> {
                todayMainTv.text = "LOADING..."
            }
            WeatherUiState.Error -> {
                // Show error UI
                // (Optional: You can show an error message or perform other UI changes)
            }
        }
    }

}
