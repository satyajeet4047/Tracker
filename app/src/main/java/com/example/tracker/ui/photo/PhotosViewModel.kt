package com.example.tracker.ui.photo

import android.content.ContentValues
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tracker.util.AppConstants
import java.text.SimpleDateFormat
import java.util.*

class PhotosViewModel : ViewModel() {

    private val photoItem : MutableLiveData<ContentValues> by lazy{
        MutableLiveData<ContentValues>()
    }

    fun photoData() : LiveData<ContentValues> = photoItem

    fun generatePhotoData()  {
        val timeStamp = SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.getDefault()).format(Date())
        photoItem.value =  ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, timeStamp.plus(AppConstants.IMAGE_EXTENSION))
            put(MediaStore.Images.Media.MIME_TYPE, AppConstants.IMAGE_MIME_TYPE)
        }
    }
}