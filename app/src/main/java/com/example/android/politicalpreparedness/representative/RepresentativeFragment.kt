package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.PoliticalPreparednessApplication
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.election.VoterInfoFragmentDirections
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import java.util.Locale
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.data.remote.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import java.util.ArrayList

class RepresentativeFragment : Fragment() {

    companion object {
        private val REQUEST_LOCATION_PERMISSION = 1
        private val TAG = RepresentativeFragment::class.java.simpleName
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
    }

    private lateinit var listAdapter: RepresentativeListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewDataBinding: FragmentRepresentativeBinding
    private val representativeViewModel by viewModels<RepresentativeViewModel> {
        RepresentativeModelFactory((requireContext().applicationContext as PoliticalPreparednessApplication).electionRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentRepresentativeBinding.inflate(inflater, container, false).apply {
            viewModel = representativeViewModel
        }
        return viewDataBinding.root
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        representativeViewModel.stateList = resources.getStringArray(R.array.states).toMutableList()

        setupRepresentativeListAdapter()
        representativeViewModel.toastMessage.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })
        representativeViewModel.fetchLocationEvent.observe(viewLifecycleOwner, Observer {
            checkLocationPermissions()
        })
        representativeViewModel.openWebViewEvent.observe(viewLifecycleOwner, Observer {
            openWebView(it)
        })
        representativeViewModel.representatives.observe(viewLifecycleOwner, Observer {
            listAdapter.submitList(it)
        })

        representativeViewModel.address.observe(viewLifecycleOwner, Observer {
            representativeViewModel.userFilledAddress.line1 = it.line1
            representativeViewModel.userFilledAddress.line2 = it.line2
            representativeViewModel.userFilledAddress.city = it.city
            representativeViewModel.userFilledAddress.state = it.state
            representativeViewModel.userFilledAddress.zip = it.zip

            viewDataBinding.addressLine1.setText(it.line1)
            viewDataBinding.addressLine2.setText(it.line2)
            viewDataBinding.city.setText(it.city)
            viewDataBinding.state.setSelection(
                resources.getStringArray(R.array.states).indexOf(it.state)
            )
            viewDataBinding.zip.setText(it.zip)

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeData(savedInstanceState)
    }

    private fun initializeData(savedInstanceState: Bundle?){
        if (savedInstanceState != null) {
            representativeViewModel._address.value = Address(
                savedInstanceState.getString("lineOne") ?: "",
                savedInstanceState.getString("lineTwo") ?: "",
                savedInstanceState.getString("city") ?: "",
                savedInstanceState.getString("state") ?: "",
                savedInstanceState.getString("zip") ?: "",)

            representativeViewModel.stateItemPosition.value = savedInstanceState.getInt("stateItemPosition")

            representativeViewModel._representatives.value = savedInstanceState.getParcelableArrayList("reps")

            viewDataBinding.rvRepresentatives.layoutManager?.onRestoreInstanceState(savedInstanceState.getParcelable("recycleState"))
        }
    }

    private fun openWebView(url: String) {
        val action =
            RepresentativeFragmentDirections.actionRepresentativeFragmentToWebViewFragment(url)
        findNavController().navigate(action)
    }

    private fun setupRepresentativeListAdapter() {
        val viewModel = viewDataBinding.viewModel
        if (viewModel != null) {
            listAdapter = RepresentativeListAdapter(viewModel)
            viewDataBinding.rvRepresentatives.adapter = listAdapter
        } else {
            Log.v(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    startIntentSenderForResult(
                        exception.resolution.intentSender,
                        REQUEST_TURN_DEVICE_LOCATION_ON, null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    requireView(),
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                fusedLocationClient.lastLocation.addOnCompleteListener {
                    it.result?.apply {
                        representativeViewModel.userFilledAddress = geoCodeLocation(this)
                        representativeViewModel._address.value = geoCodeLocation(this)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                checkDeviceLocationSettings()
            } else {
                Snackbar.make(
                    requireView(),
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(R.string.settings) {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkLocationPermissions() {
        if (isPermissionGranted()) {
            checkDeviceLocationSettings()
        } else {
            requestPermissions(
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare ?: "",
                    address.subThoroughfare ?: "",
                    address.locality ?: "",
                    address.adminArea ?: "",
                    address.postalCode ?: ""
                )
            }.first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettings(false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("address", representativeViewModel._address.value)
        outState.putString("lineOne", representativeViewModel.address.value?.line1)
        outState.putString("lineTwo", representativeViewModel.address.value?.line2)
        outState.putString("city", representativeViewModel.address.value?.city)
        outState.putString("state", representativeViewModel.address.value?.state)
        outState.putString("zip", representativeViewModel.address.value?.zip)
        outState.putInt("stateItemPosition", representativeViewModel.stateItemPosition.value ?: 0)
        outState.putParcelableArrayList("reps", representativeViewModel.representatives.value as ArrayList<out Parcelable>)
        outState.putParcelable("recycleState", viewDataBinding.rvRepresentatives.layoutManager?.onSaveInstanceState())
    }

}