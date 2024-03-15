package koslin.jan.weather.fragments

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import koslin.jan.weather.R
import koslin.jan.weather.WeatherViewModel
import koslin.jan.weather.config.SharedPreferencesKeys
import koslin.jan.weather.data.LocationData

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var defaultCityEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var textViewDefaultCity: TextView
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherViewModel = ViewModelProvider(requireActivity(), WeatherViewModel.Factory)
            .get(WeatherViewModel::class.java)

        defaultCityEditText = view.findViewById(R.id.editTextDefaultCity)
        saveButton = view.findViewById(R.id.buttonSaveDefaultCity)
        textViewDefaultCity = view.findViewById(R.id.textViewDefaultCity)

        weatherViewModel.defaultCity.observe(viewLifecycleOwner) { city ->
            textViewDefaultCity.text = city
        }

        saveButton.setOnClickListener {
            val defaultCity = defaultCityEditText.text.toString()
            handleSearch(defaultCity)
        }

        val preferencesFragmentContainer = view.findViewById<FragmentContainerView>(R.id.preferencesFragmentContainer)

        // Create and add the PreferencesFragment
        val preferencesFragment = PreferencesFragment()
        childFragmentManager.beginTransaction()
            .replace(preferencesFragmentContainer.id, preferencesFragment)
            .commit()
    }

    private fun handleSearch(cityName: String) {
        // Geocoding logic
        val geocoder = Geocoder(requireContext())
        val addresses = geocoder.getFromLocationName(cityName, 1)

        if (addresses != null && addresses.isNotEmpty()) {
            val latitude = addresses[0].latitude
            val longitude = addresses[0].longitude
            val city = addresses[0].locality

            // Update the ViewModel with the new default city
            weatherViewModel.updateLocationData(LocationData(latitude, longitude, city), true)

            // Call the API with the obtained latitude and longitude
            weatherViewModel.getWeatherData()
            Toast.makeText(requireContext(), "Default city saved", Toast.LENGTH_SHORT).show()
        } else {
            // Handle no results or error
        }
    }
}