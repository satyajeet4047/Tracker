package com.example.tracker.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.example.tracker.R
import com.example.tracker.databinding.FragmentDashboardBinding
import com.example.tracker.service.LocationService
import com.example.tracker.util.AppConstants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class DashboardFragment : Fragment() {

    private lateinit var _binding: FragmentDashboardBinding
    private var mapFragment: SupportMapFragment? = null
    private var alertDialog: AlertDialog? = null
    private var googleMap: GoogleMap? = null
    private val receiver: LocationReceiver by lazy {
        LocationReceiver()
    }

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        googleMap?.let {
            it.uiSettings.isZoomGesturesEnabled = true
        }
        requireContext().startService(locationServiceIntent)
    }

    private var locationServiceIntent: Intent? = null

    @SuppressLint("MissingPermission")
    private val _requestPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { resultMap ->
            var isGranted = true
            for (permission in resultMap.keys) {
                if (permission == Manifest.permission.ACCESS_FINE_LOCATION && resultMap[permission] != true) {
                    isGranted = false
                } else if (permission == Manifest.permission.ACCESS_COARSE_LOCATION && resultMap[permission] != true) {
                    isGranted = false
                }
            }
            if (isGranted) {
                requestLocation()
            } else {
                showDialog()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        _binding.btnSignature.setOnClickListener {
            Navigation.findNavController(_binding.root)
                .navigate(R.id.action_dashboardFragment_to_signatureFragment)
        }
        _binding.btnPhotos.setOnClickListener {
            Navigation.findNavController(_binding.root)
                .navigate(R.id.action_dashboardFragment_to_photosFragment)
        }
    }

    // Request location
    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(receiver, IntentFilter(AppConstants.LOCATION_ACTION))
        requestLocation()
    }

    //    Stop service when activity is in background
    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        requireActivity().stopService(locationServiceIntent)
    }


    /*
            Request location ->
            1. initiate map, broadcast receiver when user grants permission.
            2.show warning dialog if user deny the permission
            3. request permission and then start service to fetch it
     */
    private fun requestLocation() {
        when {
            checkPermission() -> {
                mapFragment?.getMapAsync(callback)
                locationServiceIntent = Intent(requireContext(), LocationService::class.java)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                showDialog()
            }
            else -> {
                _requestPermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun checkPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }


    private fun showDialog() {
        if (alertDialog == null) {
            alertDialog = AlertDialog.Builder(context)
                .setTitle(resources.getString(R.string.permission_dialog_title))
                .setMessage(resources.getString(R.string.permission_dialog_message))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }.create()
        }
        alertDialog?.show()
    }


    /*
           Location receiver class which updates the marker on map
     */
    inner class LocationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(AppConstants.LOCATION_BROADCAST)
            location?.let {
                val loc = LatLng(location.latitude, location.longitude)
                googleMap?.addMarker(MarkerOptions().position(loc))
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f))
            }
        }
    }


}