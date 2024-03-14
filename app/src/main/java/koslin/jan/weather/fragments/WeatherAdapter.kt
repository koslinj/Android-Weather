package koslin.jan.weather.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import koslin.jan.weather.R

class WeatherAdapter(
    private var time: List<String>,
    private var temperature: List<Double>,
    private var rain: List<Double>
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Declare the views in your item layout (e.g., TextViews)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val temperatureTextView: TextView = itemView.findViewById(R.id.temperatureTextView)
        val rainTextView: TextView = itemView.findViewById(R.id.rainTextView)
        val weatherIconImageView: ImageView = itemView.findViewById(R.id.weatherIconImageView)
    }

    fun updateData(newTime: List<String>, newTemperature: List<Double>, newRain: List<Double>) {
        time = newTime
        temperature = newTemperature
        rain = newRain
        //WAŻNE
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        // Inflate the item layout and create the view holder
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_item_layout, parent, false)
        return WeatherViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        // Bind the data to the views in each item
        holder.timeTextView.text = time[position]

        // Format temperature and rain values
        val formattedTemperature = String.format("%.1f°C", temperature[position])
        val formattedRain = String.format("%.1fmm", rain[position])

        holder.temperatureTextView.text = formattedTemperature
        holder.rainTextView.text = formattedRain

        // Set weather icons based on rain conditions (for example)
        val rainAmount = rain[position]
        if (rainAmount > 0) {
            holder.weatherIconImageView.setImageResource(R.drawable.rain_icon)
        } else {
            holder.weatherIconImageView.setImageResource(R.drawable.sun_icon)
        }
    }


    override fun getItemCount(): Int {
        // Return the number of items in your data
        return time.size
    }
}