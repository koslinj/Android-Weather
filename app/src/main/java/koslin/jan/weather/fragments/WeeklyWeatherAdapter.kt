package koslin.jan.weather.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import koslin.jan.weather.R
import koslin.jan.weather.data.SingleWeatherInfo

class WeeklyWeatherAdapter(
    private var weatherInfo: List<SingleWeatherInfo>,
    private var temperatureUnit: String
) :
    RecyclerView.Adapter<WeeklyWeatherAdapter.WeeklyForecastViewHolder>() {

    inner class WeeklyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val tv1: TextView = itemView.findViewById(R.id.time1TextView)
        val tv2: TextView = itemView.findViewById(R.id.time2TextView)
        val tv3: TextView = itemView.findViewById(R.id.time3TextView)
    }

    fun updateData(newWeatherInfo: List<SingleWeatherInfo>, newTemperatureUnit: String) {
        weatherInfo = newWeatherInfo
        temperatureUnit = newTemperatureUnit
        //WAŻNE
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyForecastViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weekly_forecast, parent, false)
        return WeeklyForecastViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WeeklyForecastViewHolder, position: Int) {
        // Bind the data to the views in each item
        val len = weatherInfo[position].time.length
        val onlyHour = weatherInfo[position].time.subSequence(len - 5, len)
        holder.tv1.text = onlyHour

        // Format temperature and rain values
        val formattedTemperature = if (temperatureUnit == "celsius") {
            String.format("%.1f°C", weatherInfo[position].temperature)
        } else {
            String.format("%.1f°F", weatherInfo[position].temperature)
        }
        val formattedRain = String.format("%.1fmm", weatherInfo[position].rain)

        holder.tv2.text = formattedTemperature
        holder.tv3.text = formattedRain

        // Set weather icons based on rain conditions (for example)
//        val rainAmount = weatherInfo[position].rain
//        if (rainAmount > 0) {
//            holder.weatherIconImageView.setImageResource(R.drawable.rain_icon)
//        } else {
//            holder.weatherIconImageView.setImageResource(R.drawable.sun_icon)
//        }
    }

    override fun getItemCount(): Int {
        return weatherInfo.size
    }
}
