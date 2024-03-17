package koslin.jan.weather.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import koslin.jan.weather.R
import koslin.jan.weather.data.SingleWeatherInfo

class WeeklyWeatherAdapter(
    private var weatherGroups:  List<List<SingleWeatherInfo>>,
    private var temperatureUnit: String
) :
    RecyclerView.Adapter<WeeklyWeatherAdapter.WeeklyForecastViewHolder>() {

    inner class WeeklyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTv1: TextView = itemView.findViewById(R.id.time1TextView)
        val timeTv2: TextView = itemView.findViewById(R.id.time2TextView)
        val timeTv3: TextView = itemView.findViewById(R.id.time3TextView)
        val tempTv1: TextView = itemView.findViewById(R.id.temp1TextView)
        val tempTv2: TextView = itemView.findViewById(R.id.temp2TextView)
        val tempTv3: TextView = itemView.findViewById(R.id.temp3TextView)
        val rainTv1: TextView = itemView.findViewById(R.id.rain1TextView)
        val rainTv2: TextView = itemView.findViewById(R.id.rain2TextView)
        val rainTv3: TextView = itemView.findViewById(R.id.rain3TextView)
        val img1: ImageView = itemView.findViewById(R.id.img1)
        val img2: ImageView = itemView.findViewById(R.id.img2)
        val img3: ImageView = itemView.findViewById(R.id.img3)

        val views1: List<TextView> = listOf(timeTv1, tempTv1, rainTv1)
        val views2: List<TextView> = listOf(timeTv2, tempTv2, rainTv2)
        val views3: List<TextView> = listOf(timeTv3, tempTv3, rainTv3)
    }

    fun updateData(newWeatherGroups: List<List<SingleWeatherInfo>>, newTemperatureUnit: String) {
        weatherGroups = newWeatherGroups
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
        val weatherGroup = weatherGroups[position]
        val len = weatherGroup[0].time.length

        holder.dateTextView.text = weatherGroup[0].time.subSequence(0, len - 6)
        // For each slot (1, 2, 3)
        for (i in 0..2) {
            val onlyHour = weatherGroup[i].time.subSequence(len - 5, len)
            val temperature = weatherGroup[i].temperature
            val rain = weatherGroup[i].rain
            val formattedTemperature = if (temperatureUnit == "celsius") {
                String.format("%.1f°C", temperature)
            } else {
                String.format("%.1f°F", temperature)
            }
            val formattedRain = String.format("%.1fmm", rain)

            // Set data to corresponding views based on slot number
            when (i) {
                0 -> {
                    setProperViews(holder.views1, holder.img1, onlyHour, formattedTemperature, formattedRain, rain)
                }
                1 -> {
                    setProperViews(holder.views2, holder.img2, onlyHour, formattedTemperature, formattedRain, rain)
                }
                2 -> {
                    setProperViews(holder.views3, holder.img3, onlyHour, formattedTemperature, formattedRain, rain)
                }
            }
        }
    }

    private fun setProperViews(
        views: List<TextView>,
        img: ImageView,
        onlyHour: CharSequence,
        formattedTemperature: String,
        formattedRain: String,
        rain: Double
    ) {
        views[0].text = onlyHour
        views[1].text = formattedTemperature
        views[2].text = formattedRain
        if (rain > 0) {
            img.setImageResource(R.drawable.rain_icon)
        } else {
            img.setImageResource(R.drawable.sun_icon)
        }
    }

    override fun getItemCount(): Int {
        return weatherGroups.size
    }
}
