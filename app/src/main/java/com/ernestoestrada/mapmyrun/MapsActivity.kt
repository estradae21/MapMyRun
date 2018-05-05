package com.ernestoestrada.mapmyrun

import android.Manifest.*
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.ernestoestrada.mapmyrun.R.id.*
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.locationManager
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import java.util.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val LOCATION_REQUEST_CODE = 101
    private var mLocationPermissionGranted = false
    private lateinit var mMap : GoogleMap
    private var locationManager :LocationManager? = null
    private var longitude : Double? = null
    private var latitude : Double? = null
    private var latLng : LatLng? = null
    private var points = ArrayList<LatLng>()
    var handler: Handler? = null
    internal var seconds = ""
    internal var minutes = ""
    internal var hours = "00"
    internal var MillisecondTime : Long = 0
    internal var StartTime : Long = 0
    internal var TimeBuff : Long = 0
    internal var UpdateTime = 0L
    internal var Seconds : Int = 0
    internal var Minutes : Int = 0
    internal var MilliSeconds : Int = 0
    internal var TimerOn :Boolean = false
    internal var isPaused :Boolean = false
    internal var TotalTime = ""
    lateinit var line : Polyline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        //bindViews()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        var actionBar = getSupportActionBar()
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar!!.setDisplayShowTitleEnabled(true)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //points = ArrayList<LatLng>()
        //locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)

        startbtn.setOnClickListener {
            if (isPaused == true) {
                StartTime = SystemClock.uptimeMillis()
                handler?.postDelayed(runnable, 0)
                TimerOn = true
                isPaused = false
                mMap.addMarker(MarkerOptions().position(latLng!!).title("Resume"))
                buttons(0)
            }

            else {
                try {
                    // Request location updates
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener);
                } catch(ex: SecurityException) {
                    Log.d("myTag", "Security Exception, no location available");
                }
                StartTime = SystemClock.uptimeMillis()
                handler?.postDelayed(runnable, 0)
                TimerOn = true
                mMap.addMarker(MarkerOptions().position(latLng!!).title("Start"))
                buttons(0)
            }
        }
        handler = Handler()

        pausebtn.setOnClickListener {
            if (TimerOn == true) {
                TimeBuff += MillisecondTime
                handler?.removeCallbacks(runnable)
                mMap.addMarker(MarkerOptions().position(latLng!!).title("Pause"))
                isPaused = true
                TimerOn = false
                buttons(1)
            }
        }

        stopbtn.setOnClickListener {
            buttons(1)
            TotalTime = "$hours:$minutes:$seconds"
            MillisecondTime = 0L
            StartTime = 0L
            TimeBuff = 0L
            UpdateTime = 0L
            Seconds = 0
            Minutes = 0
            MilliSeconds = 0
            mMap.addMarker(MarkerOptions().position(latLng!!).title("Finish"))
            handler?.removeCallbacks(runnable)
            tView.setText("00:00:00")
            runfinished()
        }
    }

    var runnable : Runnable = object : Runnable {
        override fun run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime
            UpdateTime = TimeBuff + MillisecondTime
            Seconds = (UpdateTime / 1000).toInt()
            Minutes = Seconds / 60
            Seconds = Seconds % 60
            MilliSeconds = (UpdateTime % 1000).toInt()

            if (Minutes == 60) { hours += 1 }
            if (Minutes.toString().length < 2) { minutes = "0" + Minutes.toString() }
            else { minutes = Minutes.toString() }
            if (Seconds.toString().length < 2) { seconds = "0" + Seconds.toString() }
            else { seconds = Seconds.toString() }

            tView?.text = "$hours:$minutes:$seconds"
            handler?.postDelayed(this, 0)
        }

    }

    private fun buttons(idx : Int) {
        if (idx == 0) {
            startbtn.visibility = View.INVISIBLE
            pausebtn.visibility = View.VISIBLE
            stopbtn.visibility = View.VISIBLE
        }
        if (idx == 1) {
            startbtn.visibility = View.VISIBLE
            pausebtn.visibility = View.INVISIBLE
            stopbtn.visibility = View.INVISIBLE
        }
    }

    private fun requestPermission(PermissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(PermissionType), requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    toast("Unable to show location - permission required")
                } else {
                    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                    mLocationPermissionGranted = true
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (true) {
            val isOkay = ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
            if (isOkay == PackageManager.PERMISSION_GRANTED) {
                mMap?.isMyLocationEnabled = true
                getGPS()
                //val lastknown = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                //val lat = lastknown
                //toast("$lastknown")

                //val latLng = LatLng(latitude!!, longitude!!)
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

            } else {
                requestPermission(permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE)
            }
        }
    }

    private fun getGPS() {
        val isOkay = ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        if (isOkay == PackageManager.PERMISSION_GRANTED) {
            val lastknown = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastknown != null) {
                longitude = lastknown.longitude
                latitude = lastknown.latitude
                latLng = LatLng(latitude!!, longitude!!)
            }
        }
    }

    //locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            longitude = location.longitude
            latitude = location.latitude
            latLng = LatLng(latitude!!,longitude!!)
            points.add(latLng!!)
            redrawline()
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun redrawline() {
        val options : PolylineOptions = PolylineOptions().width(8f).color(Color.GREEN).geodesic(true)
        for (i in 0 until points.size) {
            val point = points[i]
            options.add(point)
        }

        //val options = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
        for (i in 0 until points.size) {
            val point = points[i]
            options.add(point)
        }
        line  = mMap.addPolyline(options)
    }

    private fun runfinished() { }

    private fun mapstyle () {
        val mapList = arrayListOf<String>("Normal", "Satelleite", "Terrain", "Hybrid")
        selector("Which map style?", mapList, {dialogInterface, i ->
            if (i == 0) { mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL }
            else if (i == 1) { mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE }
            else if (i == 2) { mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN }
            else { mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
        //change settings to information

            R.id.map_style -> {
                mapstyle()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}



/*
    https://stackoverflow.com/questions/30249920/how-to-draw-path-as-i-move-starting-from-my-current-location-using-google-maps
    http://www.techotopia.com/index.php/Kotlin_-_Working_with_the_Google_Maps_Android_API_in_Android_Studio
    GoogleMap.MAP_TYPE_NONE﻿ – An empty grid with no mapping tiles displayed.
    GoogleMap.MAP_TYPE_NORMAL﻿ – The standard view consisting of the classic road map.
    GoogleMap.MAP_TYPE_SATELLITE﻿ – Displays the satellite imagery of the map region.
    GoogleMap.MAP_TYPE_HYBRID﻿ – Displays satellite imagery with the road maps superimposed.
    GoogleMap.MAP_TYPE_TERRAIN – Displays topographical information such as contour lines and colors.
    mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
    android programming the big nerd ranch guide 3rd
    chapter 34
    @android:drawable/ic_media_pause
    @android:drawable/ic_media_play
 */
