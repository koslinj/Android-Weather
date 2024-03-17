package koslin.jan.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CityAdapter(
    private var cities: List<String>,
    private val onItemClick: (String) -> Unit,
    private val onRemoveClick: (String) -> Unit
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityNameButton: Button = itemView.findViewById(R.id.cityNameButton)
        private val removeButton: Button = itemView.findViewById(R.id.removeFromFavButton)


        init {
            cityNameButton.setOnClickListener {
                onItemClick(cities[adapterPosition])
            }

            removeButton.setOnClickListener {
                onRemoveClick(cities[adapterPosition])
            }
        }

        fun bind(city: String) {
            cityNameButton.text = city
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position]
        holder.bind(city)
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    fun updateData(newCities: List<String>) {
        cities = newCities
        notifyDataSetChanged()
    }
}
