package dong.duan.weathernow.api

import dong.duan.weathernow.model.WeatherResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object RetrofitInstance {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"


    val openWeatherMapApi: OpenWeatherMapApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherMapApi::class.java)
    }

}

interface OpenWeatherMapApi {
    @GET("weather")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String ="metric"
    ): Call<WeatherResponse>

    @GET("forecast")
    fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = "be6dcf9307df4844cf8d1dbd2c9078ff",
        @Query("units") units: String ="metric"
    ): Call<WeatherResponse>
}