package dong.duan.weathernow.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.mobiai.base.basecode.adapter.BaseAdapter
import dong.duan.weathernow.R
import dong.duan.weathernow.databinding.ForecastItemViewBinding
import dong.duan.weathernow.model.WeatherData
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.hours

class HourForecastAdapter(var calback: (WeatherData) -> Unit) :
    BaseAdapter<WeatherData, ForecastItemViewBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = ForecastItemViewBinding.inflate(inflater, parent, false)

    fun getIndexTime(value: Int): Int {
        return when {
            value in 6 until 9 -> 6
            value in 9 until 12 -> 9
            value in 12 until 15 -> 12
            value in 15 until 18 -> 15
            value in 18 until 21 -> 18
            else -> -1
        }
    }


    @SuppressLint("SetTextI18n")
    override fun bind(binding: ForecastItemViewBinding, item: WeatherData, position: Int) {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val itemHour = item.dateTimeText.substring(11, 13).toInt()

        binding.root.setBackgroundResource(
            if (getIndexTime(currentHour) == itemHour) R.drawable.bg_forecast_view_curent
            else R.drawable.bg_forecast_view
        )

        binding.txtTime.text =
            item.dateTimeText.substring(10, 13) + if (itemHour < 17) " AM" else " PM"
        Glide.with(binding.root)
            .load("https://openweathermap.org/img/wn/${item.weatherList[0].icon}@2x.png")
            .into(binding.imgForecast)
        binding.txtDegree.text = item.mainWeatherInfo.temperature.toString() + "°"
        binding.root.setOnClickListener {
            calback(item)
        }
    }
}