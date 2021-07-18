package com.example.tracker.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.tracker.util.AppConstants
import com.google.android.gms.location.*

/**
 *   Background service which fetch the location with 2 minutes interval.
 *   It broadcast the location using local broadcast manager.
 *   Service is kept alive only when the app is in foreground.
 *   Uses handler, handler thread  to process request on worker thread instead of  main thread
 */

class LocationService : Service() {

    companion object {
        val TAG: String? = LocationService::class.java.simpleName
    }

    private lateinit var _fusedLocationClient: FusedLocationProviderClient
    private lateinit var _handler: Handler


    private val _locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                sendLocationToActivity(locationResult.lastLocation)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "in onCreate()")
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val handlerThread = HandlerThread("Handler")
        handlerThread.start()
        _handler = Handler(handlerThread.looper)
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            Log.i(TAG, "in requestLastLocation()")
            _fusedLocationClient.requestLocationUpdates(
                createLocationRequest(),
                _locationCallback,
                Looper.myLooper()
            )
        } catch (e: Exception) {
            Log.i(TAG, "in requestLastLocation error")

        }
        return START_NOT_STICKY
    }


    private fun sendLocationToActivity(lastLocation: Location) {
        Log.i(TAG, "in sendLocationToActivity ")
        val intent = Intent(AppConstants.LOCATION_ACTION).apply {
            putExtra(AppConstants.LOCATION_BROADCAST, lastLocation)
        }
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest().apply {
            interval = AppConstants.LOCATION_FETCH_INTERVAL
            fastestInterval = AppConstants.LOCATION_FAST_FETCH_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy()")
        _handler.removeCallbacksAndMessages(null)
    }

    override fun onBind(intent: Intent?): IBinder? = null

}