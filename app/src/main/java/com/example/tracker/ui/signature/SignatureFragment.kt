package com.example.tracker.ui.signature

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tracker.R
import com.example.tracker.databinding.FragmentSignatureBinding
import com.example.tracker.util.RequestStatus
import com.github.gcacace.signaturepad.views.SignaturePad
import java.lang.String


class SignatureFragment : Fragment() {

    //Initialize viewModel
    private val _viewModel: SignatureViewModel by lazy {
        ViewModelProvider(this).get(SignatureViewModel::class.java)
    }

    private lateinit var _binding: FragmentSignatureBinding
    private lateinit var _navController: NavController
    private var alertDialog: AlertDialog? = null
    private var loadingDialog: AlertDialog? = null

    private val _requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                saveData()
            } else {
                showDialog(true)
            }
        }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignatureBinding.inflate(inflater, container, false)
        return _binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                _navController.navigateUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setupUi() {
        _navController = Navigation.findNavController(_binding.root)
        _binding.btnClear.setOnClickListener {
            _binding.signatureView.clearView()
        }

        _binding.btnSave.setOnClickListener {
            when {
                checkPermission() -> {
                    saveData()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    showDialog(true)
                }
                else -> {
                    _requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }

    }

    private fun setupObservers() {

        _viewModel.getRequestStatus().observe(viewLifecycleOwner, {
            when (it) {
                RequestStatus.SUCCESS -> {
                    showProgress(false)
                    showToast(resources.getString(R.string.save_success))
                    _navController.navigateUp()
                }
                RequestStatus.FAILURE -> {
                    showToast(resources.getString(R.string.save_error))
                    showProgress(false)
                }
                RequestStatus.LOADING -> {
                    showProgress(true)
                }
            }
        })
    }


    private fun checkPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private fun saveData() {
        if(!_binding.signatureView.isEmpty) {
            if (Environment.isExternalStorageEmulated()) {
                val removable = Environment.getExternalStorageDirectory()
                val path = String.valueOf(removable)
                _viewModel.saveSignature(
                    path,
                    _binding.signatureView.signatureBitmap
                )
            } else {
                showToast(resources.getString(R.string.no_sd_card))
            }
        } else {
            showToast(resources.getString(R.string.empty_signature))
        }
    }

    private fun showToast(text: kotlin.String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }

    private fun showDialog(isVisible: Boolean) {
        if (alertDialog == null) {
            alertDialog = AlertDialog.Builder(context)
                .setTitle(resources.getString(R.string.permission_dialog_title))
                .setMessage(resources.getString(R.string.permission_dialog_message))
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.create()
        }
        if (isVisible) {
            alertDialog?.show()
        } else {
            alertDialog?.dismiss()
        }
    }

    private fun showProgress(isVisible: Boolean) {
        if (loadingDialog == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                loadingDialog = AlertDialog.Builder(context)
                    .setView(R.layout.progress_loading)
                    .create()
            }
        }

        if (isVisible) {
            loadingDialog?.show()
        } else {
            loadingDialog?.dismiss()
        }
    }
    //Clear dialogs to avoid any possible memory leak
    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.dismiss()
        loadingDialog?.dismiss()
        alertDialog = null
        loadingDialog = null
    }
}