package koslin.jan.weather.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import koslin.jan.weather.R

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}