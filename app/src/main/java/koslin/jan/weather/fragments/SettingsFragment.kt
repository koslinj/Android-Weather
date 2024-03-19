package koslin.jan.weather.fragments

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import koslin.jan.weather.R
import koslin.jan.weather.WeatherViewModel
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

        val defaultCityPrefix = resources.getString(R.string.default_city_prefix)
        weatherViewModel.defaultCity.observe(viewLifecycleOwner) { city ->
            val concatenatedCity = "$defaultCityPrefix $city"
            textViewDefaultCity.text = concatenatedCity
        }

        saveButton.setOnClickListener {
            val defaultCity = defaultCityEditText.text.toString()
            weatherViewModel.handleSearch(defaultCity, true)
            Toast.makeText(requireContext(), "Default city saved", Toast.LENGTH_SHORT).show()
        }

        val preferencesFragmentContainer = view.findViewById<FragmentContainerView>(R.id.preferencesFragmentContainer)

        // Create and add the PreferencesFragment
        val preferencesFragment = PreferencesFragment()
        childFragmentManager.beginTransaction()
            .replace(preferencesFragmentContainer.id, preferencesFragment)
            .commit()
    }
}