package com.usenergysolutions.energybroker.view.maps

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.config.OpeningHoursPatch
import com.usenergysolutions.energybroker.model.DataWrapper
import com.usenergysolutions.energybroker.model.DecisionMakerModel
import com.usenergysolutions.energybroker.model.UesPlaceModel
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.view.maps.gragments.BusinessDetailsFragment
import com.usenergysolutions.energybroker.view.maps.gragments.BusinessReviewFragment
import com.usenergysolutions.energybroker.view.maps.gragments.DecisionMakersFragment
import com.usenergysolutions.energybroker.view.maps.gragments.LocationPickerFragment
import com.usenergysolutions.energybroker.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.activity_add_business.*

// Ref: https://codinginfinite.com/android-viewpager-viewpagerindicator-example/
// Ref: https://stackoverflow.com/questions/42963445/disable-option-on-soft-keyboard-android-programmatically

class AddBusinessActivity : AppCompatActivity(), LocationPickerFragment.OnLocationPickedListener,
    BusinessDetailsFragment.OnBusinessDetailsSaveListener, /*ActivityTimeFragment.OnOpeningHoursSetListener,*/
    /*WorkingTimeFragment.OnWorkingHoursSetListener,*/
    DecisionMakersFragment.OnDecisionMakerSetListener, BusinessReviewFragment.OnBusinessConfirmReviewListener {
    private val TAG: String = "AddBusinessActivity"

    companion object {
        private const val MIN_SCALE = 0.65f
        private const val MIN_ALPHA = 0.3f
    }

    // UI Helpers
    private lateinit var adapter: MyPagerAdapter

    // Location Helper
    var latitude: Double? = null
    var longitude: Double? = null

    // Data Helper
    private val businessDetailsParams: HashMap<String, Any> = HashMap()
    private lateinit var context: Context

    // Communication
    private var viewModel: MapViewModel? = null
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Screen Configuration
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_add_business)
        context = this

        Crashlytics.log("$TAG onCreate")

        // Init communication
        viewModel = ViewModelProviders.of(this@AddBusinessActivity).get(MapViewModel::class.java)

        latitude = intent.getDoubleExtra(Constants.NAME_LATITUDE_DATA, -1.0)
        longitude = intent.getDoubleExtra(Constants.NAME_LONGITUDE_DATA, -1.0)
        if (latitude == -1.0 || longitude == -1.0) {
            Log.e(TAG, "onCreate", Exception("Latitude or Longitude are missing"))
            onBackPressed()
        } else {
            initLocation(latitude!!, longitude!!)

            adapter = MyPagerAdapter(supportFragmentManager)
            addPagerFragments()
            viewPager.adapter = adapter
            viewPager.setPageTransformer(true, this::zoomOutTransformation)
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {}
                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

                override fun onPageSelected(p0: Int) {
                    this@AddBusinessActivity.onPageSelected(p0)
                }
            })
        }
    }

    private fun clearSaveData() {
        businessDetailsParams.clear()
    }

    private fun moveToNextPage() {
        if (viewPager.currentItem < adapter.count) {
            viewPager.setCurrentItem(viewPager.currentItem + 1, true)
        }
    }

    private fun addPagerFragments() {
        if (!adapter.addFragments(LocationPickerFragment.newInstance(latitude!!, longitude!!))) {
            throw Throwable("Failed to add Location Picker Fragment")
        }
        if (!adapter.addFragments(BusinessDetailsFragment.newInstance())) {
            throw Throwable("Failed to add Business Detailed Fragment")
        }
//        if (!adapter.addFragments(ActivityTimeFragment.newInstance())) {
//            throw Throwable("Failed to add Activity Time Fragment")
//        }
//        if(!adapter.addFragments(WorkingTimeFragment.newInstance())) {
//            throw Throwable("Failed to add Working Time Fragment")
//        }
//        if (!adapter.addFragments(DecisionMakersFragment.newInstance())) {
//            throw Throwable("Failed to add Decision Maker Fragment")
//        }
        if (!adapter.addFragments(BusinessReviewFragment.newInstance())) {
            throw Throwable("Failed to add Business Review Fragment")
        }
    }

//    private fun onPageSelected(position: Int) {
//        when (position) {
//            0 -> {
//                firstDotImageView.setImageResource(R.drawable.location_on)
//                secondDotImageView.setImageResource(R.drawable.info_off)
//                thirdDotImageView.setImageResource(R.drawable.opening_off)
//                fourthDotImageView.setImageResource(R.drawable.contact_off)
//                fifthDotImageView.setImageResource(R.drawable.summary_off)
//            }
//            1 -> {
//                firstDotImageView.setImageResource(R.drawable.location_off)
//                secondDotImageView.setImageResource(R.drawable.info_on)
//                thirdDotImageView.setImageResource(R.drawable.opening_off)
//                fourthDotImageView.setImageResource(R.drawable.contact_off)
//                fifthDotImageView.setImageResource(R.drawable.summary_off)
//            }
//            2 -> {
//                firstDotImageView.setImageResource(R.drawable.location_off)
//                secondDotImageView.setImageResource(R.drawable.info_off)
//                thirdDotImageView.setImageResource(R.drawable.opening_on)
//                fourthDotImageView.setImageResource(R.drawable.contact_off)
//                fifthDotImageView.setImageResource(R.drawable.summary_off)
//            }
//            3 -> {
//                firstDotImageView.setImageResource(R.drawable.location_off)
//                secondDotImageView.setImageResource(R.drawable.info_off)
//                thirdDotImageView.setImageResource(R.drawable.opening_off)
//                fourthDotImageView.setImageResource(R.drawable.contact_on)
//                fifthDotImageView.setImageResource(R.drawable.summary_off)
//            }
//            4 -> {
//                firstDotImageView.setImageResource(R.drawable.location_off)
//                secondDotImageView.setImageResource(R.drawable.info_off)
//                thirdDotImageView.setImageResource(R.drawable.opening_off)
//                fourthDotImageView.setImageResource(R.drawable.contact_off)
//                fifthDotImageView.setImageResource(R.drawable.summary_on)
//            }
//        }
//    }

    private fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                firstDotImageView.setImageResource(R.drawable.location_on)
                secondDotImageView.setImageResource(R.drawable.info_off)
                fifthDotImageView.setImageResource(R.drawable.summary_off)
            }
            1 -> {
                firstDotImageView.setImageResource(R.drawable.location_off)
                secondDotImageView.setImageResource(R.drawable.info_on)
                fifthDotImageView.setImageResource(R.drawable.summary_off)
            }
            2 -> {
                firstDotImageView.setImageResource(R.drawable.location_off)
                secondDotImageView.setImageResource(R.drawable.info_off)
                fifthDotImageView.setImageResource(R.drawable.summary_on)
            }
        }
    }


    private fun zoomOutTransformation(page: View, position: Float) {
        when {
            position < -1 ->
                page.alpha = 0f
            position <= 1 -> {
                page.scaleX = Math.max(MIN_SCALE, 1 - Math.abs(position))
                page.scaleY = Math.max(MIN_SCALE, 1 - Math.abs(position))
                page.alpha = Math.max(MIN_ALPHA, 1 - Math.abs(position))
            }
            else -> page.alpha = 0f
        }
    }

//    // ActivityTimeFragment.OnOpeningHoursSetListener
//    override fun onOpeningHours(daysAndHours: List<DayOpeningModel>, asStrings: List<String>, isOpenNonStop: Boolean) {
//        val params: HashMap<String, Any> = HashMap()
//        params.put(Constants.OPENING_HOURS, daysAndHours)
//        params.put(Constants.OPENING_HOURS_STRINGS, asStrings)
//        params.put(Constants.OPENING_NON_STOP, isOpenNonStop) // TODO use it in the review screen
//        addBusinessDetails(params)
//    }

    // WorkingTimeFragment.OnWorkingHoursSetListener implementation
//    override fun onWorkingHours(daysAndHours: List<DayOpeningModel>, asStrings: List<String>, isOpenNonStop: Boolean) {
//        val params: HashMap<String, Any> = HashMap()
//        params.put(Constants.OPENING_HOURS, daysAndHours)
//        params.put(Constants.OPENING_HOURS_STRINGS, asStrings)
//        params.put(Constants.OPENING_NON_STOP, isOpenNonStop) // TODO use it in the review screen
//        addBusinessDetails(params)
//    }

    // BusinessDetailsFragment.OnBusinessDetailsSaveListener
    override fun BusinessDetailsSave(params: HashMap<String, Any>) {
        addBusinessDetails(params)
    }

    // Init location
    private fun initLocation(latitude: Double, longitude: Double) {
        businessDetailsParams.put(Constants.NAME_LATITUDE_DATA, latitude)
        businessDetailsParams.put(Constants.NAME_LONGITUDE_DATA, longitude)
    }

    // LocationPickerFragment.OnLocationPickedListener Implementation
    override fun onLocationPicked(latitude: Double, longitude: Double) {
        val params: HashMap<String, Any> = HashMap()
        params.put(Constants.NAME_LATITUDE_DATA, latitude)
        params.put(Constants.NAME_LONGITUDE_DATA, longitude)
        addBusinessDetails(params)
        val geoCoder = Geocoder(context)
        var addressList: MutableList<Address>? = null
        try {
            addressList = geoCoder.getFromLocation(latitude, longitude, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                businessDetailsParams.put(Constants.BUSINESS_ADDRESS, addressList[0].getAddressLine(0))
                var fragment: BusinessDetailsFragment? = adapter.getFragmentAt(1) as? BusinessDetailsFragment
                if (fragment != null) {
                    fragment.putArguments(businessDetailsParams)
                }
            }
        } catch (ex: java.lang.Exception) {
            Log.e(TAG, "initLocation", ex)
        }

    }

    // DecisionMakersFragment.OnDecisionMakerSetListener
    override fun onDecisionMakerSet(decisionMakers: List<DecisionMakerModel>) {
        val params: HashMap<String, Any> = HashMap()
        params.put(Constants.DECISION_MAKERS, decisionMakers)
        addBusinessDetails(params)
    }

    // BusinessReviewFragment.OnBusinessConfirmReviewListener
    override fun onBusinessConfirmReview(confirm: Boolean) {
        if (confirm) {
            Log.d(TAG, "send to server")
            val latitude: Double? = businessDetailsParams.get(Constants.NAME_LATITUDE_DATA) as? Double
            val longitude: Double? = businessDetailsParams.get(Constants.NAME_LONGITUDE_DATA) as? Double
            val name: String? = businessDetailsParams.get(Constants.BUSINESS_NAME) as? String
            val address: String? = businessDetailsParams.get(Constants.BUSINESS_ADDRESS) as? String
            val emails: String? = businessDetailsParams.get(Constants.BUSINESS_EMAIL) as? String
            val phones: String? = businessDetailsParams.get(Constants.BUSINESS_PHONE) as? String
            val type: Int? = businessDetailsParams.get(Constants.BUSINESS_TYPE) as? Int
            var decisionMakers: List<DecisionMakerModel>? =
                businessDetailsParams.get(Constants.DECISION_MAKERS) as? List<DecisionMakerModel>
            if (decisionMakers == null) decisionMakers = arrayListOf()
//            val openingHours: List<String>? =
//                businessDetailsParams.get(Constants.OPENING_HOURS_STRINGS) as? List<String>
            val openingHours: List<String> = OpeningHoursPatch.getOpeningHours()
            if (latitude == null || longitude == null || name == null || address == null || phones == null ||
                type == null || decisionMakers == null || openingHours == null || emails == null
            ) {
                Toast.makeText(context, "Some data is missing", Toast.LENGTH_LONG).show()
            } else {
                viewModel?.addBusiness(
                    latitude,
                    longitude,
                    name,
                    address,
                    emails,
                    phones,
                    type,
                    decisionMakers,
                    openingHours
                )
                    ?.observe(this,
                        Observer<DataWrapper<UesPlaceModel>> { t ->
                            if (t?.data != null) {
                                Toast.makeText(context, "Business saved successfully", Toast.LENGTH_LONG).show()
                                onCancelPressed()
                            } else if (t?.throwable != null) {
                                Toast.makeText(context, "Failed to save business ${t.throwable}", Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                Toast.makeText(context, "Failed to save business", Toast.LENGTH_LONG).show()  // TODO
                            }
                        })
            }
        } else {
            clearSaveData()
            onCancelPressed()
        }
    }

    // BusinessReviewFragment.OnBusinessConfirmReviewListener
    override fun sendDataToFragment(fragment: BusinessReviewFragment) {
        fragment.putArguments(businessDetailsParams)
    }

    // BusinessDetailsFragment.OnBusinessDetailsSaveListener
    private fun addBusinessDetails(details: HashMap<String, Any>?) {
        if (details != null && details.isNotEmpty()) {
            for ((key, value) in details) {
                // if (!businessDetailsParams.containsKey(key)) {
                businessDetailsParams.put(key, value)
                //}
            }
        }
        moveToNextPage()
    }

    private fun getBusinessDetailsParams(): HashMap<String, Any> {
        return businessDetailsParams
    }

    private fun showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.newInstance("Loading...")
        }
        progressDialog!!.show(supportFragmentManager, progressDialog!!::class.java.simpleName)
    }

    private fun onCancelPressed() {
        finish()
        overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
    }

    override fun onBackPressed() {
        if (viewPager.currentItem != 0) {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
        } else {
            onCancelPressed()
        }
    }

    // Disable option on soft-keyboard android programmatically
    // Ref: https://stackoverflow.com/questions/42963445/disable-option-on-soft-keyboard-android-programmatically
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN || event?.action == KeyEvent.ACTION_UP) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_UNKNOWN -> return true
                KeyEvent.KEYCODE_DPAD_RIGHT -> return true
                KeyEvent.KEYCODE_DPAD_LEFT -> return true
            }
        }
        return super.dispatchKeyEvent(event)
    }


    inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private var fragmentList: MutableList<Fragment> = ArrayList()

        override fun getItem(position: Int) = fragmentList[position]

        override fun getCount() = fragmentList.size

        fun addFragments(fragment: Fragment) = fragmentList.add(fragment)

        fun getFragmentAt(index: Int): Fragment? {
            if (index >= fragmentList.size) return null
            return fragmentList.get(index)
        }
    }
}