package dong.duan.weathernow.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.*
import dong.duan.weathernow.api.RetrofitInstance
import dong.duan.weathernow.base.BaseActivity
import dong.duan.weathernow.databinding.ActivityMainBinding
import dong.duan.weathernow.model.WeatherData
import dong.duan.weathernow.model.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {
        val LOCATION_PERMISSION_REQUEST_CODE = 10000
    }
    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun createView() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            10.0f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    callApi2(location.longitude, location.latitude) { wh ->
                        setToView(wh)
                    }
                }

            }
        )

        val fusedLocationClient = getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                callApi2(location!!.longitude, location.latitude) { wh ->
                    setToView(wh)
                }
            }
            .addOnFailureListener { e ->
                showToast(e.message.toString())
                Log.d("Error", "ERRVAL: ${e.message}")
            }
    }

    fun setToView(wh: WeatherResponse) {
        var listWeather = mutableListOf<WeatherData>()
        val currentDate = Date()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        wh.weatherDataList.forEach { item ->
            val itemDate = item.dateTimeText.substring(0, 10)
            val currentDateStr = dateFormatter.format(currentDate).substring(0, 10)

            if (itemDate == currentDateStr) {
                listWeather.add(item)
            }
        }

        var hourForecastAdapter = HourForecastAdapter() {

        }
        binding.rcvList.adapter = hourForecastAdapter
        hourForecastAdapter.setItems(listWeather)
    }


    fun callApi2(longitude: Double, latitude: Double, calback: (WeatherResponse) -> Unit) {
        val call = RetrofitInstance.openWeatherMapApi.getWeatherForecast(latitude, longitude)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    calback(weatherResponse!!)
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                showToast(t.message.toString())
                Log.d("Error", "ERRVAL: ${t.message}")
            }
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    goToSetting(this.applicationContext)
                }
            }

        }
    }


}