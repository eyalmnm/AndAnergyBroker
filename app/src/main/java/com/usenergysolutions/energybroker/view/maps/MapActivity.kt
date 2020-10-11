package com.usenergysolutions.energybroker.view.maps

import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.config.Dynamic
import com.usenergysolutions.energybroker.config.LocationHelper
import com.usenergysolutions.energybroker.model.ExtendedBusinessInfoModel
import com.usenergysolutions.energybroker.model.UesPlaceModel
import com.usenergysolutions.energybroker.services.location.locationListenerService
import com.usenergysolutions.energybroker.utils.CalendarUtils
import com.usenergysolutions.energybroker.utils.StringUtils
import com.usenergysolutions.energybroker.view.dialogs.AppExitDialog
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.view.settings.AppSettingsActivity
import com.usenergysolutions.energybroker.view.user.PersonalReportActivity
import com.usenergysolutions.energybroker.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.activity_maps.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


// Ref: https://stackoverflow.com/questions/29323317/how-to-stop-android-service-when-app-is-closed/31586477#31586477

class MapActivity : AppCompatActivity(), OnMapReadyCallback, SearchView.OnQueryTextListener,
    EasyPermissions.PermissionCallbacks {
    private val TAG: String = "MapActivity"

    // Permission Components
    companion object {
        const val RC_APP_PERMISSION: Int = 123
    }

    private val appPermissions: Array<String> = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var permissionGranted: Boolean = false

    // Location Components
    private var locationReceiver: BroadcastReceiver? = null
    private var isGpsAvailable: Boolean = false

    // Map Components
    private var map: GoogleMap? = null
    private var lat: Double = 42.0      // USA Location
    private var lng: Double = -71.0    // USA Location
    private var time: Long = 0
    private var zoom: Float = 18F
    private var marker: Marker? = null
    private var placesMarkers: MutableList<Marker> = mutableListOf<Marker>()
    private var myNewPlacesMarkers: MutableList<Marker> = mutableListOf<Marker>()

    // Search Components
    private var searchView: SearchView? = null
    private var searchMarker: Marker? = null

    // Info window components
    private var mapInfoLatng: LatLng? = null
    private var updateMap = true
    private val infoContainerAnimationDuration = 750

    // Helpers
    private lateinit var context: Context
    private lateinit var icon: BitmapDescriptor
    private lateinit var iconNoGPS: BitmapDescriptor
    private lateinit var defIcon: BitmapDescriptor
    private lateinit var newPlaceIcon: BitmapDescriptor

    // The last query time form our DB for new places
    private var lastQuery: Long = 0
    private var latLng = LatLng(lat, lng)

    private var viewModel: MapViewModel? = null

    // Loading Dialog
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.activity_maps)
        Log.d(TAG, "onCreate")
        context = this

        Crashlytics.log("\$TAG onCreate")

        // Init the ViewModel
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        // Create my marker iconStr and def iconStr for new places
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin)
        iconNoGPS = BitmapDescriptorFactory.fromResource(R.drawable.ic_person_no_gps_pin)
        defIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        newPlaceIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)

        fabGpsFix.setOnClickListener {
            updateMap = true
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), zoom))
        }

        fabAddLocation.setOnClickListener { openAddLocationScreen() }

        mapInfoContainer.setOnClickListener { mapInfoCloseButton.performClick() }

        crmButton.setOnClickListener {
            // TODO
            Toast.makeText(context, "CRM button clicked", Toast.LENGTH_SHORT).show()
        }

        newsButton.setOnClickListener {
            // TODO
            Toast.makeText(context, "News button clicked", Toast.LENGTH_SHORT).show()
        }

        profileButton.setOnClickListener {
            // Intent intent = new Intent(context, PersonalAccountActivity.class);
            val intent = Intent(context, PersonalReportActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
        }

        mapInfoCloseButton.setOnClickListener {
            val leaveAnimation = AnimationUtils.loadAnimation(context, R.anim.map_info_leave)
            leaveAnimation.duration = infoContainerAnimationDuration.toLong()
            leaveAnimation.interpolator = DecelerateInterpolator()
            leaveAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    mapInfoContainer.visibility = View.GONE
                    mapInfoTitleTextView.text = ""
                    mapInfoAddressTextView.text = ""
                    mapInfoLatng = null
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            mapInfoContainer.startAnimation(leaveAnimation)
        }

        mapInfoDirectionButton.setOnClickListener {
            val temp = mapInfoLatng
            mapInfoCloseButton.performClick()
            if (temp != null) {
                openDirectionMap(temp.latitude, temp.longitude)
            }
        }

        mapInfoCallButton.setOnClickListener {
            mapInfoCloseButton.performClick()
            if (!mapInfoPhoneTextView.text.toString().isEmpty()) {
                openDialer(mapInfoPhoneTextView.text.toString())
            }
        }

        mapInfoMoreButton.setOnClickListener(View.OnClickListener { v ->
            mapInfoCloseButton.performClick()
            val placeId = v.tag as String
            if (!StringUtils.isNullOrEmpty(placeId)) {
                val intent = Intent(context, BusinessInfoActivity::class.java)
                intent.putExtra(Constants.PLACE_ID, placeId)
                startActivity(intent)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        })

        mapInfoEditButton.setOnClickListener(View.OnClickListener { v ->
            mapInfoCloseButton.performClick()
            val placeId = v.tag as String
            if (!StringUtils.isNullOrEmpty(placeId)) {
                val intent = Intent(context, BusinessEditActivity::class.java)
                intent.putExtra(Constants.PLACE_ID, placeId)
                startActivity(intent)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        })

        // Init Map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        getPermissions()
        if (map != null) {
            loadingUesPlaces()
        }
    }

    private fun openAddLocationScreen() {
        val intent = Intent(context, AddBusinessActivity::class.java) // AddPlaceActivity.class);
        val extras = Bundle()
        extras.putDouble(Constants.NAME_LATITUDE_DATA, lat)
        extras.putDouble(Constants.NAME_LONGITUDE_DATA, lng)
        intent.putExtras(extras)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady googleMap: $googleMap")
        map = googleMap
        if (map != null) {
            map!!.uiSettings?.isZoomControlsEnabled = true
            //map.setPadding(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.map_ui_marging_bottom));  // Set Ui Settings position
            map!!.setMinZoomPreference(5f)
            map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), zoom))
            setOnMarkerClickListener(map!!)
            setOnPoiClick(map!!)
            setMapDragListener(map!!)
        }
    }

    private fun setMapDragListener(map: GoogleMap) {
        map.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                updateMap = false
            }
        }
    }

    private fun updateMap(action: String, lat: Double, lng: Double, time: Long) {
        Log.d(TAG, "updateMap action: $action")

        // Remove the previous marker, ddd a marker and move the camera
        marker?.remove()

        if (time != 0L && action == Constants.LOCATION_AVAILABLE) {
            latLng = LatLng(lat, lng)
            marker = map?.addMarker(MarkerOptions().position(latLng).icon(icon))
            val timeDiff = System.currentTimeMillis() - lastQuery
            Log.d(TAG, "UpdateMap placesMarkers.size: " + placesMarkers.size + " timeDiff: " + timeDiff)
            if (placesMarkers.isEmpty() && timeDiff > 750 || timeDiff > 30000) {
                loadingUesPlaces()
                lastQuery = System.currentTimeMillis()
            }
        } else {
            marker = map?.addMarker(MarkerOptions().position(latLng).icon(iconNoGPS))
        }

        if (updateMap) {
            map?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    private fun loadingUesPlaces() {
        removeOldMarkers()
        removeOldNewPlaces()
        showLoadingDialog()
        viewModel?.getNearByPlaces(lat, lng)?.observe(this,
            Observer { listDataWrapper ->
                progressDialog?.dismiss()
                if (listDataWrapper != null) {
                    if (listDataWrapper.data != null) {
                        if (listDataWrapper.data?.places != null) {
                            addPlaces(listDataWrapper.data?.places!!)
                            loadingMyNewPlaces()
                        } else {
                            //Toast.makeText(context, "Places not found near your location", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val msg = if (listDataWrapper.throwable != null) listDataWrapper.throwable!!.message else ""
                        Toast.makeText(context, "Failed to loading places data $msg", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun loadingMyNewPlaces() {
        showLoadingDialog()
        viewModel?.getPlacesByUserIdAndCreationDate(CalendarUtils.getCurrentLocalDateString())?.observe(this,
            Observer { listDataWrapper ->
                progressDialog?.dismiss()
                if (listDataWrapper != null && listDataWrapper.data != null) {
                    if (listDataWrapper.data!!.error == true) {
                        Toast.makeText(
                            context,
                            "Failed to loading new added places data " + listDataWrapper.data!!.errorMsg,
                            Toast.LENGTH_LONG
                        )
                            .show()
                    } else {
                        addMyNewPlaces(listDataWrapper.data!!.places)
                    }
                } else {
                    var msg: String? = null
                    if (listDataWrapper != null) {
                        msg = if (listDataWrapper.throwable != null) listDataWrapper.throwable!!.message else ""
                    }
                    Toast.makeText(context, "Failed to loading new added places data " + msg!!, Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    private fun addMyNewPlaces(data: List<UesPlaceModel>?) {
        if (data == null) return
        val placeIcon = newPlaceIcon
        for (place in data) {
            if (place == null || place.latitude == null || place.longitude == null) continue
            val position = LatLng(place.latitude!!, place.longitude!!)
            val marker = map?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(place.businessName)
                    .snippet(place.businessAddress)
                    .icon(placeIcon)
            )
            if (marker != null) {
                marker.tag = place.placeId
                myNewPlacesMarkers.add(marker)
            }
        }
    }

    private fun addPlaces(data: List<UesPlaceModel>) {
        var placeIcon = defIcon
        for (place in data) {
            if (place.businessType != null) {
                val resId = LocationHelper.getImageByPlaceType(place.businessType!!)
                if (resId != 0) {
                    placeIcon = BitmapDescriptorFactory.fromResource(resId)
                }
            }
            if (place.latitude == null || place.longitude == null) continue
            val position = LatLng(place.latitude!!, place.longitude!!)
            val marker = map?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(place.businessName)
                    .snippet(place.businessAddress)
                    .icon(placeIcon)
            )
            if (marker != null) {
                marker.tag = place.placeId
                placesMarkers.add(marker)
            }
        }
    }

    private fun removeOldMarkers() {
        for (marker in placesMarkers) {
            marker.remove()
        }
        placesMarkers.clear()
    }

    private fun removeOldNewPlaces() {
        for (marker in myNewPlacesMarkers) {
            marker.remove()
        }
        myNewPlacesMarkers.clear()
    }

    private fun setOnMarkerClickListener(map: GoogleMap) {
        map.setOnMarkerClickListener { marker ->
            Log.d(TAG, "setOnMarkerClickListener")
            mapInfoLatng = marker.position
            // mapInfoContainer.setVisibility(View.VISIBLE);
            mapInfoTitleTextView.text = null // (R.string.dropped_pin);
            mapInfoImageView.setImageDrawable(null)
            mapInfoImageView.visibility = View.INVISIBLE
            mapInfoAddressTextView.text = null // (snippet);
            mapInfoPhoneTextView.text = null
            mapInfoCallButton.isEnabled = false
            mapInfoMoreButton.tag = null
            mapInfoEditButton.tag = null

            if (marker.title != null) {
                mapInfoTitleTextView.text = marker.title
            }
            if (marker.tag != null) {
                val placeId = marker.tag as Int?
                Log.d(TAG, "onMarkerClick place: " + placeId!!)

                getPlaceData(placeId)
            }
            true
        }
    }

    // Get place data from Our database
    private fun getPlaceData(placeId: Int) {
        showLoadingDialog()
        viewModel?.getPlaceData(placeId.toString())?.observe(this,
            Observer { dataWrapper ->
                progressDialog?.dismiss()
                if (dataWrapper != null) {
                    if (dataWrapper.data != null) {
                        handlePlaceClick(dataWrapper.data!!)
                    } else {
                        val msg = if (dataWrapper.throwable == null) "" else dataWrapper.throwable!!.message
                        Toast.makeText(context, "Failed to retrieve place's data $msg", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun handlePlaceClick(data: ExtendedBusinessInfoModel) {
        val businessTypes = data.getType()
        val enterAnimation = AnimationUtils.loadAnimation(context, R.anim.map_info_enter)
        enterAnimation.duration = infoContainerAnimationDuration.toLong()
        enterAnimation.interpolator = DecelerateInterpolator()
        mapInfoContainer.visibility = View.VISIBLE
        mapInfoContainer.startAnimation(enterAnimation)
        if (businessTypes != null && businessTypes.size > 0) {
            var type = businessTypes[0]
            if (!StringUtils.isNumber(type)) {
                type = LocationHelper.getTypeByTypeName(type).toString()
            }
            val iconId = LocationHelper.getImageByPlaceType(Integer.parseInt(type))
            mapInfoImageView.visibility = View.VISIBLE
            Glide.with(context).load(iconId).into(mapInfoImageView)
        }
        mapInfoAddressTextView.text = data.getBusinessAddress()
        mapInfoPhoneTextView.text = data.getBusinessPhone()
        mapInfoCallButton.isEnabled = !StringUtils.isNullOrEmpty(data.getBusinessPhone())
        mapInfoMoreButton.tag = data.getPlaceId()
        mapInfoEditButton.tag = data.getPlaceId()
        mapInfoEditButton.visibility = View.VISIBLE
    }

    private fun setOnPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { pointOfInterest ->
            Log.d(TAG, "setOnPoiClick")
            mapInfoLatng = pointOfInterest.latLng
            mapInfoMoreButton.tag = null
            showLoadingDialog()

            viewModel?.getPoiData(pointOfInterest.placeId)?.observe(this@MapActivity,
                Observer { extendedBusinessInfoModelDataWrapper ->
                    progressDialog?.dismiss()
                    if (extendedBusinessInfoModelDataWrapper!!.data != null) {
                        handlePoiClick(extendedBusinessInfoModelDataWrapper.data!!)
                    } else {
                        Toast.makeText(this@MapActivity, "Failed to retrice business data!", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun handlePoiClick(data: ExtendedBusinessInfoModel) {
        Log.d(TAG, "handleMarkerClick place")
        val enterAnimation = AnimationUtils.loadAnimation(context, R.anim.map_info_enter)
        enterAnimation.duration = infoContainerAnimationDuration.toLong()
        enterAnimation.interpolator = DecelerateInterpolator()
        mapInfoContainer.visibility = View.VISIBLE
        mapInfoContainer.startAnimation(enterAnimation)
        mapInfoTitleTextView.text = data.getBusinessName()
        val iconUrl = data.getIcon()
        if (!StringUtils.isNullOrEmpty(iconUrl)) {
            mapInfoImageView.visibility = View.VISIBLE
            Glide.with(context).load(iconUrl).into(mapInfoImageView)
        }
        mapInfoAddressTextView.text = data.businessAddressStr
        mapInfoPhoneTextView.text = data.businessPhoneStr
        mapInfoCallButton.isEnabled = !StringUtils.isNullOrEmpty(data.businessPhoneStr)
        mapInfoMoreButton.tag = data.placeIdStr
        mapInfoEditButton.visibility = View.GONE
    }

    private fun openDirectionMap(dest_lat: Double, dest_long: Double) {
        val intent = Intent(
            android.content.Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?daddr=$dest_lat,$dest_long")
        )
        startActivity(intent)
    }

    private fun openDialer(phoneNumber: String) {
        if (!StringUtils.isNullOrEmpty(phoneNumber)) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }
    }

    private fun registerLocalLocationReceiver() {
        if (locationReceiver != null) {
            val locationIntentFilter = IntentFilter()
            locationIntentFilter.addAction(Constants.LOCATION_NOT_AVAILABLE)
            locationIntentFilter.addAction(Constants.LOCATION_AVAILABLE)
            LocalBroadcastManager.getInstance(context).registerReceiver(locationReceiver!!, locationIntentFilter)
        }
    }

    private fun startLocationMonitoring() {
        Log.d(TAG, "startLocationMonitoring")
        // GPS reception listener
        locationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                isGpsAvailable = Constants.LOCATION_AVAILABLE.equals(action!!, ignoreCase = true)
                if (isGpsAvailable) {
                    lat = intent.getDoubleExtra(Constants.NAME_LATITUDE_DATA, lat)
                    lng = intent.getDoubleExtra(Constants.NAME_LONGITUDE_DATA, lng)
                    time = intent.getLongExtra(Constants.NAME_SAMPLE_TIME, 0)
                }
                updateMap(action, lat, lng, time)
            }
        }
        registerLocalLocationReceiver()
        val locationServiceIntent = Intent(context, locationListenerService::class.java)
        locationServiceIntent.putExtra(Constants.UUID, Dynamic.uuid)
        // locationServiceIntent.addFlags(ServiceInfo.FLAG_STOP_WITH_TASK);
        if (hasAppPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(locationServiceIntent)
            } else {
                context.startService(locationServiceIntent)
            }
            //startService(locationServiceIntent);
        } else {
            EasyPermissions.requestPermissions(
                this,
                context.getString(R.string.must_garnt_permission),
                RC_APP_PERMISSION,
                *appPermissions
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)

        // Add Search widget to action bar
        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        // Assumes current activity is the searchable activity
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        // searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView?.setOnQueryTextListener(this)

        return true
    }

    // SearchView.OnQueryTextListener Implementation
    override fun onQueryTextSubmit(query: String): Boolean {
        Log.d(TAG, "onQueryTextSubmit query: $query")
        if (searchMarker != null) searchMarker?.remove()
        val goeCoder = Geocoder(context)
        var addressList: List<Address>? = null
        try {
            addressList = goeCoder.getFromLocationName(query, 1)
        } catch (ex: Exception) {
            Log.e(TAG, "onQueryTextSubmit", ex)
        }

        if (addressList != null && !addressList.isEmpty()) {
            val address = addressList[0]
            val latLng = LatLng(address.latitude, address.longitude)
            searchMarker = map?.addMarker(MarkerOptions().position(latLng).title("Marker"))
            map?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            updateMap = false
            if (searchView != null) {
                hideKeyBoard(searchView!!)
            }
            return true
        } else {
            Toast.makeText(context, "can not find $query", Toast.LENGTH_LONG).show()
        }
        return false
    }

    // SearchView.OnQueryTextListener Implementation
    override fun onQueryTextChange(newText: String): Boolean {
        //Log.d(TAG, "onQueryTextChange newText: " + newText);
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Change the map typeList based on the user's selection.
        when (item.itemId) {
            R.id.normal_map -> {
                map?.mapType = GoogleMap.MAP_TYPE_NORMAL
                return true
            }
            R.id.hybrid_map -> {
                map?.mapType = GoogleMap.MAP_TYPE_HYBRID
                return true
            }
            R.id.satellite_map -> {
                map?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                return true
            }
            R.id.terrain_map -> {
                map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                return true
            }
            R.id.settings -> {
                openSettingsScreen()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun hideKeyBoard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun openSettingsScreen() {
        val intent = Intent(context, AppSettingsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    public override fun onPause() {
        super.onPause()
        stopLocationMonitoring()
    }

    private fun showLoadingDialog() {
        try {
            progressDialog?.dismiss()
            if (progressDialog == null) {
                progressDialog = ProgressDialog()
            }
            progressDialog?.show(supportFragmentManager, "progressDialog")
        } catch (ex: IllegalStateException) {
            Log.e(TAG, "showLoadingDialog", ex)
        }
    }

    private fun stopLocationMonitoring() {
        // Stop listening to Location changes
        unregisterLocalLocationReceiver()
        //        Intent locationServiceIntent = new Intent(context, locationListenerService.class);
        //        stopService(locationServiceIntent);
    }

    private fun unregisterLocalLocationReceiver() {
        if (null != locationReceiver) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(locationReceiver!!)
        }
    }

    /**
     * Display any typeList of DialogFragment
     */
    private fun showDialog(dialog: DialogFragment) {
        dialog.show(supportFragmentManager, dialog.javaClass.simpleName)
    }

    private fun showExitDialog() {
        val appExitDialog = AppExitDialog()
        appExitDialog.show(supportFragmentManager, "AppExitDialog")
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    override fun onBackPressed() {
        showExitDialog()
    }

    @AfterPermissionGranted(RC_APP_PERMISSION)
    fun getPermissions() {
        if (hasAppPermissions()) {
            Log.d(TAG, "appPermissionsTask")
            startLocationMonitoring()
        } else {
            EasyPermissions.requestPermissions(
                this,
                context.getString(R.string.must_garnt_permission),
                RC_APP_PERMISSION,
                *appPermissions
            )
        }
    }


// ************************************   EasyPermissions   ************************************

    private fun hasAppPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, *appPermissions)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult")

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
        // Check if it not happen when permissions have been pre granted

        permissionGranted = true
        startLocationMonitoring()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size)

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (!permissionGranted) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                AppSettingsDialog.Builder(this).build().show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult")

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            val yes = getString(R.string.yes)
            val no = getString(R.string.no)

            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(
                this,
                getString(
                    R.string.returned_from_app_settings_to_activity,
                    if (hasAppPermissions()) yes else no
                ),
                Toast.LENGTH_LONG
            ).show()

            // Check if it not happen when permissions have been pre granted
        }
    }

}