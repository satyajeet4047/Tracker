package com.example.tracker.ui.signature

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tracker.util.RequestStatus
import java.io.File


class SignatureViewModel : ViewModel() {

    private val requestStatus by lazy {
        MutableLiveData<RequestStatus>()
    }

    fun getRequestStatus(): LiveData<RequestStatus> = requestStatus

    /**
     * save bitmap to file into sd card
     * writeBitmap can throw error
     */
    fun saveSignature(path: String, bitmap: Bitmap) {
        requestStatus.postValue(RequestStatus.LOADING)
        try {

            File(path, "signature.png").apply {
                writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 85)
            }
            requestStatus.postValue(RequestStatus.SUCCESS)

        } catch (exception: Exception) {
            requestStatus.postValue(RequestStatus.FAILURE)
        }
    }

    /**
     * Extension function which writes bitmap to file
     *    use -> this resource and then closes it down correctly
     */
    @Throws
    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }
}