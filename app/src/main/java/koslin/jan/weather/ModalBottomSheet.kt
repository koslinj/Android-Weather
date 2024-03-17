package koslin.jan.weather

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import koslin.jan.weather.config.Keys

class ModalBottomSheet : BottomSheetDialogFragment(R.layout.sheet) {

    private lateinit var weatherViewModel: WeatherViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherViewModel = ViewModelProvider(requireActivity(), WeatherViewModel.Factory)
            .get(WeatherViewModel::class.java)

        val behavior = (dialog as BottomSheetDialog).behavior
        // Now you can work with the behavior
        // Retrieve favorite cities from SharedPreferences
        val favoriteCities = getFavoriteCities()

        // Display favorite cities in the bottom sheet
        val recyclerView = view.findViewById<RecyclerView>(R.id.favoriteCitiesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = CityAdapter(favoriteCities) { cityName ->
            // Handle item click here
            handleCityItemClick(cityName)
            dismiss()
        }
    }

    private fun handleCityItemClick(cityName: String) {
        // Update the current city in the ViewModel
        weatherViewModel.handleSearch(cityName, false)

    }

    private fun getFavoriteCities(): List<String> {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet(Keys.FAVOURITE_CITIES_KEY, setOf("a", "b", "c", "d", "e", "f", "zgierz", "poznan", "warszawa", "gdansk", "sopot", "gdynia", "krakow", "paryz"))?.toList() ?: listOf("zgierz", "poznan", "warszawa", "gdansk", "sopot", "gdynia", "krakow", "paryz")
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}