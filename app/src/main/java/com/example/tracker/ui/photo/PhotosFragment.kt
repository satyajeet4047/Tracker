    package com.example.tracker.ui.photo

    import android.Manifest
    import android.app.AlertDialog
    import android.content.pm.PackageManager
    import android.os.Bundle
    import android.provider.MediaStore
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.core.content.ContextCompat
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.ViewModelProvider
    import androidx.recyclerview.widget.GridLayoutManager
    import com.example.tracker.R
    import com.example.tracker.databinding.FragmentPhotosBinding
    import com.example.tracker.ui.photo.adapter.PhotoListAdapter

    /**
     *  Fragment to show list of photos
     *  for no photo is selected shows empty list message
     *  for multiple selected photos show photos in gridlayout
     *  TODO - Keep history of selected photos
     */
    class PhotosFragment : Fragment() {

        //initialize view model
        private val viewModel: PhotosViewModel by lazy {
            ViewModelProvider(this).get(PhotosViewModel::class.java)
        }
        private lateinit var _binding: FragmentPhotosBinding

        private var alertDialog: AlertDialog? = null
        private lateinit var _adapter: PhotoListAdapter

        //Permission launcher to get permission request result
        // onActivityResult is deprecated
        private val _requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { resultMap ->

                for (i in resultMap.keys) {
                    if (i == Manifest.permission.CAMERA && resultMap[i] == true) {
                        openCamera()
                    } else if (i == Manifest.permission.WRITE_EXTERNAL_STORAGE && resultMap[i] == true) {
                        openPhotos()
                    }
                }
            }

    //Permission launcher to get multiple photos from files
    private val _requestPhotos =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            for (i in it) {
                _adapter.submitList(it)
                showEmptyView(false)
            }
        }

    //Permission launcher to open camera
    private val _requestCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            showToast(getString(R.string.photo_save_success))
        } else {
            showToast(getString(R.string.something_went_wrong))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotosBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.photoData().observe(viewLifecycleOwner, { contentValues ->
            contentValues.let {
                _requestCamera.launch(
                    context?.contentResolver?.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        it
                    )
                )
            }
        })
    }

    private fun setupUi() {

        _adapter = PhotoListAdapter()
        with(_binding.recyclerView) {
            adapter = _adapter
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
        }

        _binding.fabCamera.setOnClickListener {
            when {
                checkPermission(Manifest.permission.CAMERA) -> {
                    openCamera()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    showDialog()
                }
                else -> {
                    _requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                }
            }
        }

        _binding.fabPhotos.setOnClickListener {
            when {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    openPhotos()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    showDialog()
                }
                else -> {
                    _requestPermissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (_adapter.currentList.isNullOrEmpty()) {
            showEmptyView(true)
        }
    }

    private fun showEmptyView(isVisible: Boolean) {
        if (isVisible) {
            _binding.llEmptyView.root.visibility = View.VISIBLE
            _binding.recyclerView.visibility = View.GONE
        } else {
            _binding.llEmptyView.root.visibility = View.GONE
            _binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun openPhotos() {
        _requestPhotos.launch("image/*")
    }

    private fun openCamera() {
        viewModel.generatePhotoData()
    }


    private fun checkPermission(permission: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireActivity(), permission
        )
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }

    //Dialog shows warning if permission is not granted
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

    }