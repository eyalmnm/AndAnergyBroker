package com.usenergysolutions.energybroker.view.maps.gragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.config.LocationHelper
import com.usenergysolutions.energybroker.model.DataWrapper
import com.usenergysolutions.energybroker.model.UesPlaceModel
import com.usenergysolutions.energybroker.model.incoming.map.NearByPlacesResponse
import com.usenergysolutions.energybroker.utils.CalendarUtils
import com.usenergysolutions.energybroker.viewmodel.MapViewModel


// Ref: https://stackoverflow.com/questions/22032815/how-to-get-google-maps-object-inside-a-fragment
// Ref: https://stackoverflow.com/questions/16536414/how-to-use-mapview-in-android-using-google-map-v2
// Ref: https://stackoverflow.com/questions/15433820/mapfragment-in-fragment-alternatives
// Ref: https://stackoverflow.com/questions/7603058/how-to-change-the-position-of-the-map-controller-in-mapview-in-android?noredirect=1&lq=1


class LocationPickerFragment : AddBusinessBaseFragment(), OnMapReadyCallback {
    private val TAG: String = "LocationPickerFragment"

    // Selected Location
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var zoom: Float = 18F

    var mapView: MapView? = null
    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private lateinit var nextButton: Button

    // Communication
    var viewModel: MapViewModel? = null

    // Helpers
    private var icon: BitmapDescriptor? = null
    private lateinit var defIcon: BitmapDescriptor
    private lateinit var newPlaceIcon: BitmapDescriptor

    private var placesMarkers: MutableList<Marker> = mutableListOf<Marker>()
    private var myNewPlacesMarkers: MutableList<Marker> = mutableListOf<Marker>()

    // Activity Fragment communication interface
    interface OnLocationPickedListener {
        fun onLocationPicked(latitude: Double, longitude: Double)
    }

    private var listener: OnLocationPickedListener? = null

    // Static Creator
    companion object {

        fun newInstance(latitude: Double, longitude: Double): LocationPickerFragment {
            val args: Bundle = Bundle()
            args.putDouble(Constants.NAME_LATITUDE_DATA, latitude)
            args.putDouble(Constants.NAME_LONGITUDE_DATA, longitude)
            val fragment: LocationPickerFragment = LocationPickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnLocationPickedListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        viewModel = ViewModelProviders.of(this@LocationPickerFragment).get(MapViewModel::class.java)

        // Create my marker iconStr
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_24dp)
        defIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        newPlaceIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_location_picker, null)

        if (arguments != null) {
            latitude = arguments?.getDouble(Constants.NAME_LATITUDE_DATA, 0.0)
            longitude = arguments?.getDouble(Constants.NAME_LONGITUDE_DATA, 0.0)
        }

        initMapView(view, savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextButton = view.findViewById(R.id.nextButton)
        nextButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                saveData()
            }
        })

        // Bind Map Components
        val fabGpsFix = view.findViewById<FloatingActionButton>(R.id.fabGpsFix)
        fabGpsFix.setOnClickListener(View.OnClickListener {
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude!!, longitude!!), zoom
                )
            )
        })
    }

    private fun initMapView(view: View, savedInstanceState: Bundle?) {
        // Gets the MapView from the XML layout and creates it
        mapView = view.findViewById(com.usenergysolutions.energybroker.R.id.mapView) as MapView
        mapView?.onCreate(savedInstanceState)

        mapView?.getMapAsync(this)
    }


    // OnMapReadyCallback implementation
    override fun onMapReady(googleMap: GoogleMap?) {
        Log.d(TAG, "onMapReady")
        map = googleMap!!
        map.uiSettings?.isZoomControlsEnabled = true
        map.setPadding(
            0,
            0,
            0,
            resources.getDimensionPixelOffset(R.dimen.map_ui_marging_bottom)
        ) //(DimenUtils.convertPixelsToDp(1000F, context).toInt()))
        if (latitude != null && longitude != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude!!, longitude!!), zoom))
            marker = map.addMarker(initMarkerOptions(LatLng(latitude!!, longitude!!)))
            map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude!!, longitude!!)))
            getNearByPlaces(latitude!!, longitude!!)
        }
        setOnMapClickListener(map)
    }

    private fun getNearByPlaces(latitude: Double, longitude: Double) {
        viewModel?.getNearByPlaces(latitude, longitude)?.observe(this,
            Observer<DataWrapper<NearByPlacesResponse>> { t ->
                if (t?.data != null) {
                    if (t.data?.places != null) {
                        addPlaces(t.data?.places!!)
                        loadingMyNewPlaces()
                    } else {
                        //Toast.makeText(context, "Places not found near your location", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val msg = if (t?.throwable != null) t.throwable!!.message else ""
                    Toast.makeText(context, "Failed to loading places data $msg", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun loadingMyNewPlaces() {
        viewModel?.getPlacesByUserIdAndCreationDate(CalendarUtils.getCurrentLocalDateString())?.observe(this,
            Observer { listDataWrapper ->
                if (listDataWrapper?.data != null) {
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
            val marker = map.addMarker(
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
            val marker = map.addMarker(
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


    // Init Marker Options
    private fun initMarkerOptions(latLng: LatLng): MarkerOptions? {
        var markerOptions: MarkerOptions = MarkerOptions().title("Me").icon(icon).position(latLng)
        return markerOptions
    }

    private fun setOnMapClickListener(googleMap: GoogleMap) {
        googleMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng?) {
                Log.d(TAG, "onMapClick")
                marker?.remove()
                marker = map.addMarker(initMarkerOptions(latLng!!))
                latitude = latLng.latitude
                longitude = latLng.longitude
            }
        })
    }

    override fun saveData() {
        if (listener != null) {
            listener?.onLocationPicked(latitude!!, longitude!!)
        }
    }

    override fun onResume() {
        mapView?.onResume()
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

}