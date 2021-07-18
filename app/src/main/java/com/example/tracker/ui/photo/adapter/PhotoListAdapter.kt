package com.example.tracker.ui.photo.adapter

import android.net.Uri
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tracker.databinding.ItemPhotoListBinding
import com.squareup.picasso.Picasso


/**
 *  Adapter class which populates list of photos from URI
 */
class PhotoListAdapter : ListAdapter<Uri,PhotoListAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Uri>(){
    override fun areItemsTheSame(oldItem: Uri, newItem: Uri) = oldItem.encodedPath == newItem.encodedPath
    override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean = oldItem == newItem
}) {

    class ViewHolder(binding : ItemPhotoListBinding) : RecyclerView.ViewHolder(binding.root) {
        private val _binding : ItemPhotoListBinding = binding

        //Shows image and name
        fun bindData(uri: Uri){
            val context = _binding.root.context
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val idx = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor?.moveToFirst()
           _binding.textView.text = idx?.let { cursor.getString(it)}
            cursor?.close()
            Picasso.get()
                .load(uri)
                .fit()
                .into(_binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemPhotoListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindData(getItem(position))
    }
}