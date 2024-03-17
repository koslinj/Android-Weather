package koslin.jan.weather.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import koslin.jan.weather.R
import koslin.jan.weather.data.SingleWeatherInfo

class WeatherAdapter(
    private var weatherInfo: List<SingleWeatherInfo>,
    private var temperatureUnit: String,
    private var windSpeedUnit: String
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Declare the views in your item layout (e.g., TextViews)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val temperatureTextView: TextView = itemView.findViewById(R.id.temperatureTextView)
        val rainTextView: TextView = itemView.findViewById(R.id.rainTextView)
        val windSpeedTextView: TextView = itemView.findViewById(R.id.windSpeedTextView)
        val weatherIconImageView: ImageView = itemView.findViewById(R.id.weatherIconImageView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    fun updateData(newWeatherInfo: List<SingleWeatherInfo>, newTemperatureUnit: String, newWindSpeedUnit: String) {
        weatherInfo = newWeatherInfo
        temperatureUnit = newTemperatureUnit
        windSpeedUnit = newWindSpeedUnit
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
        val len = weatherInfo[position].time.length
        val onlyHour = weatherInfo[position].time.subSequence(len - 5, len)
        holder.timeTextView.text = onlyHour

        if (onlyHour == "00:00" || position == 0) {
            holder.dateTextView.text = weatherInfo[position].time.subSequence(0, len - 6)
            holder.dateTextView.visibility = View.VISIBLE
        } else {
            holder.dateTextView.visibility = View.GONE
        }

        // Format temperature, wind speed and rain values
        val formattedTemperature = if (temperatureUnit == "celsius") {
            String.format("%.1f°C", weatherInfo[position].temperature)
        } else {
            String.format("%.1f°F", weatherInfo[position].temperature)
        }
        val formattedWindSpeed = when (windSpeedUnit) {
            "kmh" -> String.format("%.1f Km/h", weatherInfo[position].windSpeed)
            "ms" -> String.format("%.1f m/s", weatherInfo[position].windSpeed)
            else -> String.format("%.1f Mph", weatherInfo[position].windSpeed)
        }
        val formattedRain = String.format("%.1fmm", weatherInfo[position].rain)

        holder.temperatureTextView.text = formattedTemperature
        holder.rainTextView.text = formattedRain
        holder.windSpeedTextView.text = formattedWindSpeed

        // Set weather icons based on rain conditions (for example)
        val rainAmount = weatherInfo[position].rain
        if (rainAmount > 0) {
            holder.weatherIconImageView.setImageResource(R.drawable.rain_icon)
        } else {
            holder.weatherIconImageView.setImageResource(R.drawable.sun_icon)
        }
    }


    override fun getItemCount(): Int {
        // Return the number of items in your data
        return weatherInfo.size
    }
}