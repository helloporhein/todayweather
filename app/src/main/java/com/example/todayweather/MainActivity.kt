package com.example.todayweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        private const val RESQUEST_CODE_PERMISSION_LOCATION = 100
    }

    private val progressBar by lazy {
        findViewById<ProgressBar>(R.id.progressBar)
    }

    private val tvTemperature by lazy {
        findViewById<TextView>(R.id.tvTemperature)
    }

    private val ivWeatherStatus by lazy {
        findViewById<ImageView>(R.id.tvWeatherStatus)
    }

    private val tvCityName by lazy {
        findViewById<EditText>(R.id.tvCityName)
    }

    private val buttonSearch by lazy {
        findViewById<Button>(R.id.buttonSearch)
    }
    private val tvError by lazy {
        findViewById<TextView>(R.id.tvError)
    }
    private val tvReset by lazy {
        findViewById<Button>(R.id.btnReset)
    }

    private val retrofit by lazy {
        RetrofitInstanceFactory.instance()
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        buttonSearch.setOnClickListener {
            val cityName = tvCityName.text.toString()
            executeNetworkCall(cityName = cityName)
        }
        tvReset.setOnClickListener {
            getLocation()
        }
        getLocation()

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        Log.i("MainActivity.onCreate","permission granted")
                        val locationManager: LocationManager =
                                this@MainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val location =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        Log.i("MainActivity.onCreate", location?.latitude.toString())

                            executeNetworkCall(
                                latitude = location?.latitude.toString(),
                                longitude = location?.longitude.toString()

                            )

                    }

                    override fun onPermissionRationaleShouldBeShown(
                            p0: PermissionRequest?,
                            p1: PermissionToken?
                    ) {
                        Log.i("MainActivity.onCreate","permission shown")
                        showError()

                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Log.i("MainActivity.onCreate","permission denied")

                        showError()
                    }

                })
            .check()
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE

        tvTemperature.visibility = View.GONE
        tvCityName.visibility = View.GONE
        ivWeatherStatus.visibility = View.GONE
        buttonSearch.visibility = View.GONE
        tvError.visibility = View.GONE
        tvReset.visibility = View.GONE

    }

    private fun showData(
            temperature: String,
            cityName: String,
            weatherIcon: String
    ) {
        tvTemperature.text = "$temperature°C"
        tvCityName.setText(cityName)

        Glide.with(this)
                .load(weatherIcon)
                .into(ivWeatherStatus)

        progressBar.visibility = View.GONE
        tvTemperature.visibility = View.VISIBLE
        tvCityName.visibility = View.VISIBLE
        ivWeatherStatus.visibility = View.VISIBLE
        buttonSearch.visibility = View.VISIBLE

    }

    private fun showError(){
        progressBar.visibility = View.GONE
            tvTemperature.visibility = View.GONE
            tvCityName.visibility = View.GONE
            ivWeatherStatus.visibility = View.GONE
            buttonSearch.visibility = View.GONE
            tvError.visibility = View.VISIBLE
            tvReset.visibility = View.VISIBLE


        }




    private fun executeNetworkCall(
            latitude: String,
            longitude: String

    ) {
        showLoading()
        val openWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)

        openWeatherMapApi.geoCoordinate(
            latitude = latitude,
            longitude = longitude

        )
                .enqueue(object : Callback<OpenWeatherMapResponse> {
                    override fun onFailure(
                            call: Call<OpenWeatherMapResponse>,
                            t: Throwable
                    ) {
                        t.printStackTrace()
                        //showError()
                    }

                    override fun onResponse(
                            call: Call<OpenWeatherMapResponse>,
                            response: Response<OpenWeatherMapResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()
                                    ?.let { OpenWeatherMapResponse ->
                                        Log.i("Response", OpenWeatherMapResponse.toString())

                                        val iconUrl = OpenWeatherMapResponse.weatherList.getOrNull(0)?.icon
                                                ?: ""
                                        val fullUrl = "https://openweathermap.org/img/wn/$iconUrl@2x.png"
                                        showData(
                                                temperature = OpenWeatherMapResponse.main.temp,
                                                cityName = OpenWeatherMapResponse.name,
                                                weatherIcon = fullUrl
                                        )
                                    }

                        }else{
                            showError()
                        }
                    }

                    } )

    }

    private fun executeNetworkCall(cityName: String) {
        showLoading()
        val openWeatherMapApi = retrofit.create(OpenWeatherMapApi::class.java)

        openWeatherMapApi.getByCityName(
            cityName = cityName

        )
                .enqueue(object : Callback<OpenWeatherMapResponse> {
                    override fun onFailure(
                            call: Call<OpenWeatherMapResponse>,
                            t: Throwable
                    ) {
                        t.printStackTrace()
                        //showError()
                    }

                    override fun onResponse(
                            call: Call<OpenWeatherMapResponse>,
                            response: Response<OpenWeatherMapResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()
                                    ?.let { OpenWeatherMapResponse ->
                                        Log.i("Response", OpenWeatherMapResponse.toString())

                                        val iconUrl = OpenWeatherMapResponse.weatherList.getOrNull(0)?.icon
                                                ?: ""
                                        val fullUrl = "https://openweathermap.org/img/wn/$iconUrl@2x.png"
                                        showData(
                                                temperature = OpenWeatherMapResponse.main.temp,
                                                cityName = OpenWeatherMapResponse.name,
                                                weatherIcon = fullUrl
                                        )
                                    }

                        }else{

                            showError()
                        }
                    }
                })

    }
}
