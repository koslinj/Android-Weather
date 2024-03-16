package koslin.jan.weather.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import koslin.jan.weather.R
import koslin.jan.weather.WeatherViewModel
import koslin.jan.weather.data.LocationData

class PreferencesFragment : PreferenceFragmentCompat() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var preferencesChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        weatherViewModel = ViewModelProvider(requireActivity(), WeatherViewModel.Factory)
            .get(WeatherViewModel::class.java)

        preferencesChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            weatherViewModel.updateTemperatureUnit()
            weatherViewModel.getWeatherData()
        }
    }

    override fun onStart() {
        super.onStart()
        // Register the preference change listener
        weatherViewModel.defaultPreferences.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    override fun onStop() {
        super.onStop()
        // Unregister the preference change listener
        weatherViewModel.defaultPreferences.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

}