package com.example.aplikacjapogodowa.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplikacjapogodowa.R
import com.example.aplikacjapogodowa.viewmodel.DailyForecastAdapter
import com.example.aplikacjapogodowa.viewmodel.HourlyForecastAdapter
import com.example.aplikacjapogodowa.viewmodel.WeatherVM
import kotlinx.android.synthetic.main.main_screen.*
import kotlinx.android.synthetic.main.main_screen.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class SeniorFragment  : Fragment() {
    // ViewModel:
    private lateinit var weatherVM : WeatherVM

    // Hourly Forecast:
    private lateinit var hourlyForecastAdapter : HourlyForecastAdapter
    private lateinit var hourlyForecastLayoutManager : LinearLayoutManager
    private lateinit var hourlyForecastRecyclerView : RecyclerView

    // Daily Forecast:
    private lateinit var dailyForecastAdapter : DailyForecastAdapter
    private lateinit var dailyForecastLayoutManager : LinearLayoutManager
    private lateinit var dailyForecastRecyclerView : RecyclerView

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        weatherVM = ViewModelProvider(requireActivity()).get(WeatherVM::class.java)
        hourlyForecastLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        hourlyForecastAdapter = HourlyForecastAdapter(weatherVM.currentHourlyForecast, "Senior")
        dailyForecastLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        dailyForecastAdapter = DailyForecastAdapter(weatherVM.currentDailyForecast, "Senior", requireContext())
        val view =  inflater.inflate(R.layout.senior_screen, container, false)

        // Displaying current weather:
        weatherVM.currentWeather.observe(viewLifecycleOwner, {
            if (it.cod == "200")
            {
                // Main:
                view.tv_city.text = it.name
                view.tv_currentTemperature.text = "${it.main!!.temp.roundToInt()}°C"
                view.tv_feelsLike.text = "Odczuwalne: ${it.main.feels_like.roundToInt()}°C"
                view.tv_description.text = it.weather[0].description.capitalize(Locale.ROOT)

                // Icon:
                val url = "https://openweathermap.org/img/wn/${it.weather[0].icon}@4x.png"
                Glide.with(view.iv_currentWeatherIcon).load(url).centerCrop().into(view.iv_currentWeatherIcon)

                // Details:
                view.tv_pressureValue.text = "${it.main.pressure} hPa"
                view.tv_sunriseValue.text = SimpleDateFormat("HH:mm").format(Date(it.sys!!.sunrise * 1000))
                view.tv_sunsetValue.text = SimpleDateFormat("HH:mm").format(Date(it.sys.sunset * 1000))

                weatherVM.setForecasts(it.coord!!.lat, it.coord.lon)
            }
        })

        // Displaying hourly forecast for next 24 hours:
        weatherVM.currentHourlyForecast.observe(viewLifecycleOwner, { hourlyForecastAdapter.notifyDataSetChanged() })

        // Displaying daily forecast for next 7 weeks:
        weatherVM.currentDailyForecast.observe(viewLifecycleOwner, { dailyForecastAdapter.notifyDataSetChanged() })

        // Default city:
        weatherVM.setCurrentWeather("Bytom")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hourlyForecastRecyclerView = rv_hourly.apply {
            this.layoutManager = hourlyForecastLayoutManager
            this.adapter = hourlyForecastAdapter
        }

        dailyForecastRecyclerView = rv_daily.apply {
            this.layoutManager = dailyForecastLayoutManager
            this.adapter = dailyForecastAdapter
        }

        // Top Bar Actions:
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    // Handle Searching
                    true
                }
                R.id.findWithGPS -> {
                    // Handle Locating
                    true
                }
                R.id.elderlyMode -> {
                    view.findNavController().navigate(R.id.action_seniorFragment_to_mainFragment)
                    true
                }
                else -> false
            }
        }
    }
}