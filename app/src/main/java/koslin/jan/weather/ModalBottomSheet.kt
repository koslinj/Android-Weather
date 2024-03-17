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
    private lateinit var cityAdapter: CityAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherViewModel = ViewModelProvider(requireActivity(), WeatherViewModel.Factory)
            .get(WeatherViewModel::class.java)

        val behavior = (dialog as BottomSheetDialog).behavior
        val favoriteCities = getFavoriteCities()

        // Initialize the adapter
        cityAdapter = CityAdapter(favoriteCities,
            onItemClick = { cityName ->
                handleCityItemClick(cityName)
                dismiss()
            },
            onRemoveClick = { cityName ->
                removeCityFromFavorites(cityName)
            }
        )

        // Display favorite cities in the bottom sheet
        val recyclerView = view.findViewById<RecyclerView>(R.id.favoriteCitiesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = cityAdapter
    }

    private fun handleCityItemClick(cityName: String) {
        // Update the current city in the ViewModel
        weatherViewModel.handleSearch(cityName, false)

    }

    private fun removeCityFromFavorites(cityName: String) {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val favoriteCitiesSet = sharedPreferences.getStringSet(Keys.FAVOURITE_CITIES_KEY, setOf())?.toMutableSet()
        favoriteCitiesSet?.remove(cityName)
        sharedPreferences.edit().putStringSet(Keys.FAVOURITE_CITIES_KEY, favoriteCitiesSet).apply()

        val updatedFavoriteCities = favoriteCitiesSet?.toList() ?: emptyList()
        cityAdapter.updateData(updatedFavoriteCities)
    }

    private fun getFavoriteCities(): List<String> {
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet(Keys.FAVOURITE_CITIES_KEY, setOf("a", "b", "c", "d", "e", "f", "zgierz", "poznan", "warszawa", "gdansk", "sopot", "gdynia", "krakow", "paryz"))?.toList() ?: listOf("zgierz", "poznan", "warszawa", "gdansk", "sopot", "gdynia", "krakow", "paryz")
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}