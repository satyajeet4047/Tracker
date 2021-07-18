package com.example.tracker.util

object AppConstants {
        const val IMAGE_EXTENSION = ".jpeg"
        const val IMAGE_MIME_TYPE = "image/jpeg"
        const val DATE_FORMAT = "yyyyMMdd_HHmmss$$$"
        const val SIGNATURE_PATH = "/storage/emulated/0/DCIM/Camera"
        const val SIGNATURE_FILE_NAME = "Signature.jepg"
        const val LOCATION_BROADCAST = "LocationBroadcast"
        const val LOCATION_ACTION = "LocationAction"
        const val LOCATION_FETCH_INTERVAL : Long = 1000*60*5
        const val LOCATION_FAST_FETCH_INTERVAL : Long = 1000*60*2
}