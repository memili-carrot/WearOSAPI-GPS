package com.example.wearosgps.complication

import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.google.android.gms.location.*

/**
 * Complication service that displays real-time GPS location data.
 */
class MainComplicationService : SuspendingComplicationDataSourceService() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: String = "0.0"
    private var longitude: String = "0.0"

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationUpdates()
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        if (type != ComplicationType.SHORT_TEXT) {
            return null
        }
        return createComplicationData("0.0, 0.0", "GPS Data")
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val locationData = getLocationData()
        return createComplicationData(locationData, "Current Location")
    }

    private fun createComplicationData(text: String, contentDescription: String) =
        ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder(contentDescription).build()
        ).build()

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    latitude = "%.5f".format(it.latitude)
                    longitude = "%.5f".format(it.longitude)
                }
            }
        }

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun getLocationData(): String {
        return "$latitude, $longitude"
    }
}