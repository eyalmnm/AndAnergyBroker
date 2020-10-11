package com.usenergysolutions.energybroker.view.maps

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.crashlytics.android.Crashlytics
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.config.LocationHelper
import com.usenergysolutions.energybroker.model.PlaceTypeModel
import com.usenergysolutions.energybroker.view.adapters.PlacesTypeAdapter
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.view.maps.dialogs.OpeningHoursDialog
import com.usenergysolutions.energybroker.viewmodel.MapViewModel

class AddPlaceActivity : AppCompatActivity(), OnMapReadyCallback, OpeningHoursDialog.OnOpeningHoursSetListener {
    private val TAG: String = "AddPlaceActivity"

    // Selected Location
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var zoom: Float = 18F
    private lateinit var map: GoogleMap
    private var marker: Marker? = null

    // UI Components
    private lateinit var addPlaceFormContainer: View

    private lateinit var businessNameEditText: EditText
    private lateinit var businessAddressEditText: EditText
    private lateinit var businessPhoneEditText: EditText
    private lateinit var typeSpinner: Spinner

    private lateinit var contactNameEditText: EditText
    private lateinit var contactEmailEditText: EditText
    private lateinit var contactPhoneEditText: EditText

    private lateinit var cancelButton: TextView
    private lateinit var saveButton: TextView
    private lateinit var addOpeningButton: TextView

    // Places typeList Spinner helpers
    private var adapter: PlacesTypeAdapter? = null
    private var placeTypeSArray: ArrayList<PlaceTypeModel>? = null
    private var placeType: PlaceTypeModel? = null

    // Helpers
    private var context: Context? = null
    private var icon: BitmapDescriptor? = null
    private var openingHours: List<String>? = null

    // Communication
    private lateinit var mapViewModel: MapViewModel
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)
        Log.d(TAG, "onCreate")

        Crashlytics.log("$TAG onCreate")

        context = this
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        // Create my marker iconStr
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_24dp)

        if (intent != null) {
            latitude = intent.getDoubleExtra(Constants.NAME_LATITUDE_DATA, 0.0)
            longitude = intent.getDoubleExtra(Constants.NAME_LONGITUDE_DATA, 0.0)
        }

        addPlaceFormContainer = findViewById(R.id.addPlaceFormContainer)

        businessNameEditText = findViewById(R.id.businessNameET)
        businessAddressEditText = findViewById(R.id.businessAddressET)
        businessPhoneEditText = findViewById(R.id.businessPhoneET)
        typeSpinner = findViewById(R.id.typeSpinner)

        contactNameEditText = findViewById(R.id.contactNameEditText)
        contactEmailEditText = findViewById(R.id.contactEmailEditText)
        contactPhoneEditText = findViewById(R.id.contactPhoneEditText)

        cancelButton = findViewById(R.id.cancelButton)
        saveButton = findViewById(R.id.saveButton)
        addOpeningButton = findViewById(R.id.addOpeningButton)


        // addPlaceFormContainer.visibility = View.GONE  // TODO

        // Init types Spinner
        placeTypeSArray = LocationHelper.getPlacesTypesArray()
        adapter = PlacesTypeAdapter(this, R.layout.add_place_spinner_item, placeTypeSArray!!)
        typeSpinner.adapter = adapter
        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                placeType = placeTypeSArray?.get(position)
            }
        }
        placeType = placeTypeSArray?.get(0)
        typeSpinner.setSelection(0, true)

        saveButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(context, "This Function, saveButton, temporarily canceled", Toast.LENGTH_LONG).show()
                Log.e(TAG, "This Function, saveButton, temporarily canceled")
                TODO("This Function, saveButton, temporarily canceled")
//                val businessNameStr: String = businessNameEditText.text.toString()
//                val businessAddressStr: String = businessAddressEditText.text.toString()
//                val businessPhoneStr: String = businessPhoneEditText.text.toString()
//                val businessType: String = placeType?.id?.toString()!!
//                val contactName: String = contactNameEditText.text.toString()
//                val contactEmail: String = contactEmailEditText.text.toString()
//                val contactPhone: String = contactPhoneEditText.text.toString()
//                if (progressDialog == null) {
//                    progressDialog = ProgressDialog.newInstance("Login...")
//                }
//                showDialog(progressDialog!!)
//                mapViewModel.addPlace(
//                    latitudeDbl, longitudeDbl, businessNameStr, businessAddressStr, businessPhoneStr, businessType,
//                    contactName, contactEmail, contactPhone, openingHours
//                ).observe(this@AddPlaceActivity,
//                    Observer<DataWrapper<UesPlaceModel>> { t ->
//                        progressDialog!!.dismiss()
//                        if (t?.data != null) {
//                            Toast.makeText(context, "${t.data?.businessNameStr} added successfully", Toast.LENGTH_LONG)
//                                .show()
//                            onBackPressed()
//                        } else {
//                            Toast.makeText(context, "Adding place failed ${t?.throwable!!.message}", Toast.LENGTH_LONG)
//                                .show()
//                        }
//                    })
            }
        })

        // Cancel graging when form is shown
        addPlaceFormContainer.setOnTouchListener { v, event -> true }

        cancelButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                resetForm()
                addPlaceFormContainer.visibility = View.GONE
            }
        })

        addOpeningButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                showAddOpeningHoursDialog()
            }
        })

        // Init Map
        var mapFragment: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun showAddOpeningHoursDialog() {
        val openingHoursDialog: OpeningHoursDialog = OpeningHoursDialog()
        openingHoursDialog.show(supportFragmentManager, OpeningHoursDialog::class.java.simpleName)
    }

    // OpeningHoursDialog.OnOpeningHoursSetListener implementation
    override fun onOpeningHours(daysAndHours: List<String>) {
        openingHours = daysAndHours
    }

    private fun resetForm() {
        businessNameEditText.text = null
        businessAddressEditText.text = null
        businessPhoneEditText.text = null
        placeType = placeTypeSArray?.get(0)
        typeSpinner.setSelection(0, true)

        contactNameEditText.text = null
        contactEmailEditText.text = null
        contactPhoneEditText.text = null

        openingHours = null
    }

    // OnMapReadyCallback implementation
    override fun onMapReady(googleMap: GoogleMap?) {
        Log.d(TAG, "onMapReady")
        map = googleMap!!
        map.uiSettings?.isZoomControlsEnabled = true
        map.setMinZoomPreference(5F)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
        marker = map.addMarker(initMarkerOptions(LatLng(latitude, longitude)))
        map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
        setOnMapClickListener(map)
        setOnMarkerClickListener(map)
    }

    private fun setOnMarkerClickListener(googleMap: GoogleMap) {
        googleMap.setOnMarkerClickListener { marker ->
            marker?.remove()
            latitude = marker?.position?.latitude!!
            longitude = marker.position?.longitude!!
            addPlaceFormContainer.visibility = View.VISIBLE
            true
        }
    }

    private fun setOnMapClickListener(googleMap: GoogleMap) {
        googleMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng?) {
                Log.d(TAG, "onMapClick")
                marker?.remove()
                marker = map.addMarker(initMarkerOptions(latLng!!))
                latitude = latLng.latitude
                longitude = latLng.longitude
                addPlaceFormContainer.visibility = View.VISIBLE
            }
        })
    }

    // Init Marker Options
    private fun initMarkerOptions(latLng: LatLng): MarkerOptions? {
        var markerOptions: MarkerOptions = MarkerOptions().title("Me").icon(icon).position(latLng)
        return markerOptions
    }

    private fun showDialog(dialog: DialogFragment?) {
        dialog!!.show(supportFragmentManager, dialog::class.java.simpleName)
    }

    // Add animation to back press
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
    }
}