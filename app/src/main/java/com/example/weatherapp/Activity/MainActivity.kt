package com.example.weatherapp.Activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.PowerApps.R
import com.example.PowerApps.databinding.ActivityMainBinding
import com.example.weatherapp.model.CurrentResponseApi
import com.example.weatherapp.viewModel.WeatherViewModel
import com.github.matteobattilana.weather.PrecipType
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calender by lazy {
        Calendar.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        binding.apply {
            var lat = 51.50
            val lon = -0.12
            var name = "London"

            cityText.text = name
            progressBar.visibility = View.VISIBLE

            weatherViewModel.loadCurrentWeather(lat, lon, "metric")
                .enqueue(object :
                    retrofit2.Callback<CurrentResponseApi> {
                    override fun onResponse(
                        call: Call<CurrentResponseApi>,
                        response: Response<CurrentResponseApi>
                    ) {

                        if (response.isSuccessful) {
                            val data = response.body()
                            progressBar.visibility = View.GONE
                            detailLayout.visibility = View.VISIBLE

                            data?.let {
                                statusText.text = it.weather?.get(0)?.main ?: "-"
                                windText.text = it.wind?.speed?.let {
                                    Math.round(it).toString()
                                } + "Km"
                                currentTempText.text =
                                    it.main?.temp?.let { Math.round(it).toString() } + "°"

                                maxTempTxt.text =
                                    it.main?.tempMax?.let { Math.round(it).toString() } + "°"

                                minTemTpText.text =
                                    it.main?.tempMin?.let { Math.round(it).toString() } + "°"

                                humidityText.text = it.main?.humidity?.toString() + "%"

                                val drawable = if (isNightNow()) R.drawable.night_bg
                                else {
                                    setDynamicallyWallpaper(it.weather?.get(0)?.icon ?: "-")
                                }

                                bgImage.setImageResource(drawable)
                                setEffectRainSnow(it.weather?.get(0)?.icon ?: "-")

                            }

                        }

                    }

                    override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                    }

                })

        }


    }

    private fun isNightNow(): Boolean {
        return calender.get(Calendar.HOUR_OF_DAY) >= 18
    }

    private fun setEffectRainSnow(icon: String) {
        when (icon.dropLast(1)) {

            "01" -> {

                initWeatherView(PrecipType.CLEAR)


            }

            "02", "03", "04" -> {

                initWeatherView(PrecipType.CLEAR)


            }

            "09", "10", "11" -> {

                initWeatherView(PrecipType.RAIN)


            }

            "13" -> {

                initWeatherView(PrecipType.SNOW)


            }

            "50" -> {

                initWeatherView(PrecipType.CLEAR)

            }


        }


    }

    private fun setDynamicallyWallpaper(icon: String): Int {
        return when (icon.dropLast(1)) {

            "01" -> {

                initWeatherView(PrecipType.CLEAR)
                R.drawable.snow_bg

            }

            "02", "03", "04" -> {

                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg

            }

            "09", "10", "11" -> {

                initWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg

            }

            "13" -> {

                initWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg

            }

            "50" -> {

                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg

            }


            else -> 0

        }


    }

    private fun initWeatherView(type: PrecipType) {
        binding.weatherView.apply {
            setWeatherData(type)
            angle = 20
            emissionRate = 100.0f
        }
    }

}