package koslin.jan.weather.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import koslin.jan.weather.R
import koslin.jan.weather.WeatherViewModel
import koslin.jan.weather.data.LocationData

class PreferencesFragment : PreferenceFragmentCompat() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var preferencesChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private var oldValueMap: MutableMap<String, Any?> = mutableMapOf()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        weatherViewModel = ViewModelProvider(requireActivity(), WeatherViewModel.Factory)
            .get(WeatherViewModel::class.java)

        for (key in weatherViewModel.defaultPreferences.all.keys) {
            oldValueMap[key] = weatherViewModel.defaultPreferences.all[key]
        }

        var isFirstTime = true

        preferencesChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (weatherViewModel.isNetworkAvailable()) {
                // If there's internet connection, update preferences and fetch weather data
                weatherViewModel.updateWindSpeedUnit()
                weatherViewModel.updateTemperatureUnit()
                weatherViewModel.getWeatherData()
                oldValueMap[key!!] = sharedPreferences.all[key]
            } else {
                // If there's no internet connection, notify the user and revert preference change
                if(isFirstTime){
                    weatherViewModel.showCustomToast(requireContext(), R.string.no_internet_prefs)
                    isFirstTime = false
                } else {
                    isFirstTime = true
                }
                // Revert the preference change if already made
                val oldValue = oldValueMap[key]
                when (oldValue) {
                    is String -> {
                        sharedPreferences.edit().putString(key, oldValue).apply()
                        // Manually update the UI to reflect the reverted preference change using SimpleSummaryProvider
                        findPreference<ListPreference>(key!!)?.apply {
                            value = oldValue // Set the value back to the old value
                        }
                    }
                    is Int -> {
                        sharedPreferences.edit().putInt(key, oldValue).apply()
                        // Manually update the UI to reflect the reverted preference change using SimpleSummaryProvider
                        findPreference<ListPreference>(key!!)?.apply {
                            value = oldValue.toString() // Set the value back to the old value
                        }
                    }
                }
            }
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