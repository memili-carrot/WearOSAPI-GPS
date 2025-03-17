package com.example.wearosgps.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude by mutableStateOf("Fetching...")
    private var longitude by mutableStateOf("Fetching...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        setContent {
            GPSWearOSApp(latitude, longitude)
        }
    }

    private fun fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    latitude = it.latitude.toString()
                    longitude = it.longitude.toString()
                    saveLocationToJson(it.latitude, it.longitude)
                }
            }
        }
    }

    private fun saveLocationToJson(lat: Double, lon: Double) {
        val jsonObject = JSONObject().apply {
            put("latitude", lat)
            put("longitude", lon)
        }

        val file = File(getExternalFilesDir(null), "gps_data.json")
        try {
            FileWriter(file).use { writer ->
                writer.write(jsonObject.toString(4))  // JSON pretty print
                writer.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

@Composable
fun GPSWearOSApp(latitude: String, longitude: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("GPS Location", style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
        Text("Latitude: $latitude", modifier = Modifier.padding(8.dp))
        Text("Longitude: $longitude", modifier = Modifier.padding(8.dp))
    }
}
