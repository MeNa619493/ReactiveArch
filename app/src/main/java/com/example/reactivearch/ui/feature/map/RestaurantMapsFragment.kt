package com.example.reactivearch.ui.feature.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.example.reactivearch.R
import com.example.reactivearch.core.common.BaseFragment
import com.example.reactivearch.core.common.DataState
import com.example.reactivearch.core.navigation.AppNavigator
import com.example.reactivearch.core.navigation.Screen
import com.example.reactivearch.databinding.FragmentRestaurantMapsBinding
import com.example.reactivearch.domain.dto.RequestDto
import com.example.reactivearch.domain.entity.Restaurant
import com.example.reactivearch.domain.error.Failure
import com.example.reactivearch.ui.drag.IDragCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber
import javax.inject.Inject

@RuntimePermissions
@AndroidEntryPoint
class RestaurantMapsFragment : BaseFragment(), GoogleMap.OnMarkerClickListener, IDragCallback {

    private var _binding : FragmentRestaurantMapsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var appNavigator: AppNavigator
    private val mapViewModel : MapViewModel by viewModels()
    private var googleMap : GoogleMap? = null

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        googleMap.setMinZoomPreference(15f)
        googleMap.setOnMarkerClickListener(this)
        observerRestaurants()
    }

    private val locationSettingsScreen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            getCurrentLocation()
        }

    private val applicationSettingsScreen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            getCurrentLocation()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantMapsBinding.inflate(inflater,container,false)
        _binding?.draggableLayout?.setDrag(this)
        mapViewModel.fragmentRecreated = (savedInstanceState!=null)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        getCurrentLocationWithPermissionCheck()
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun getCurrentLocation() {
        if (isLocationEnabled()) {
            getLastKnownLocation {
                // log the location
                Timber.e("available lat , long: %s,%s", it.latitude, it.longitude)
                //call foursquare api to get restaurants
                val currentLatLng = LatLng(it.latitude ,it.longitude)
                val currentBounds = googleMap?.projection?.visibleRegion?.latLngBounds
                if (currentBounds!=null)
                    mapViewModel.getRestaurants(RequestDto(currentLatLng,currentBounds))
            }
        } else {
            MaterialAlertDialogBuilder(getRootActivity())
                .setTitle(getString(R.string.location_not_enabled))
                .setMessage(getString(R.string.enable_location))
                .setPositiveButton(getString(R.string.enable)) { dialog, _ ->
                    openSettingsScreen()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.deny)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun openSettingsScreen() {
        locationSettingsScreen.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showRationaleForLocation(request: PermissionRequest) {
        MaterialAlertDialogBuilder(getRootActivity())
            .setMessage(getString(R.string.location_alert))
            .setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                request.proceed()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.deny)) { dialog, _ ->
                request.cancel()
                dialog.dismiss()
            }.show()
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    fun onDenyAskLocation() {
        Toast.makeText(
            getRootActivity(),
            getString(R.string.location_denied),
            Toast.LENGTH_SHORT
        ).show()
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    fun onNeverAskLocation() {
        val openApplicationSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        openApplicationSettings.data = Uri.fromParts("package", activity?.packageName, null)
        applicationSettingsScreen.launch(openApplicationSettings)
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("this.onRequestPermissionsResult(requestCode, grantResults)")
    )
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        this.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        mapViewModel.markers[marker]
        appNavigator.navigateTo(Screen.RESTAURANT)
        return false
    }

    private fun observerRestaurants(){
        mapViewModel.restaurantsState.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    handleLoading(false)
                    renderMarkers(it.data)
                }

                is DataState.Error -> {
                    handleLoading(false)
                    if (it.error is Failure.NetworkConnectionError)
                        displayError(getString(R.string.network_connection))
                    else
                        displayError(getString(R.string.general_error))
                }

                is DataState.Loading -> {
                    handleLoading(true)
                }
            }
        }
    }

    private fun renderMarkers(restaurants : List<Restaurant>){
        val newRestaurant = mapViewModel.getNewRestaurants(restaurants)
        newRestaurant.forEach { restaurant ->
            val loc = LatLng(restaurant.latitude, restaurant.longitude)
            val marker = googleMap?.addMarker(MarkerOptions().position(loc).title(restaurant.name))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLng(loc))
            if (marker!=null) mapViewModel.markers[marker] = restaurant
        }
        if (newRestaurant.isEmpty() && mapViewModel.fragmentRecreated) {
            mapViewModel.markers.values.forEach { restaurant ->
                val loc = LatLng(restaurant.latitude, restaurant.longitude)
                googleMap?.addMarker(MarkerOptions().position(loc).title(restaurant.name))
                googleMap?.moveCamera(CameraUpdateFactory.newLatLng(loc))
            }
        }
    }

    private fun handleLoading(isLoading:Boolean){
        binding.progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

    private fun displayError(message:String?){
        message?.let {
            Snackbar.make(binding.mainView,message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDrag() {
        if (mapViewModel.fragmentRecreated)
            mapViewModel.fragmentRecreated = false
        val currentLatLng = googleMap?.cameraPosition?.target
        val currentBounds= googleMap?.projection?.visibleRegion?.latLngBounds
        mapViewModel.resetRestaurantState()
        if (currentBounds!=null && currentLatLng!=null)
            mapViewModel.getRestaurants(RequestDto(currentLatLng,currentBounds))
    }
}