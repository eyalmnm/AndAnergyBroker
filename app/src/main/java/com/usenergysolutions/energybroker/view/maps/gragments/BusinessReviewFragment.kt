package com.usenergysolutions.energybroker.view.maps.gragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.config.LocationHelper
import com.usenergysolutions.energybroker.model.DayOpeningModel
import com.usenergysolutions.energybroker.model.DecisionMakerModel
import com.usenergysolutions.energybroker.ui.DecisionMakerDisplayView
import kotlinx.android.synthetic.main.fragment_business_review.*

// Ref: https://stackoverflow.com/questions/10024739/how-to-determine-when-fragment-becomes-visible-in-viewpager
// Ref: https://stackoverflow.com/questions/37949024/kotlin-typecastexception-null-cannot-be-cast-to-non-null-type-com-midsizemango

class BusinessReviewFragment : Fragment() {
    private val TAG: String = "BusinessReviewFragment"

    interface OnBusinessConfirmReviewListener {
        fun onBusinessConfirmReview(confirm: Boolean)
        fun sendDataToFragment(fragment: BusinessReviewFragment)
    }

    private var listener: OnBusinessConfirmReviewListener? = null

    // Data Holder
    private var params: HashMap<String, Any>? = null


    // Static Creator
    companion object {

        fun newInstance(): BusinessReviewFragment {
            return BusinessReviewFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnBusinessConfirmReviewListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        userVisibleHint = false
        val view = inflater.inflate(com.usenergysolutions.energybroker.R.layout.fragment_business_review, null)
        return view
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            Log.d(TAG, "Visible")
            // if (listener != null) listener?.sendDataToFragment(this)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            Log.d(TAG, "Visible")
            if (listener != null) listener?.sendDataToFragment(this)
        } else {
            Log.d(TAG, "Hidden")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton.setOnClickListener {
            if (listener != null) {
                listener?.onBusinessConfirmReview(true)
            }
        }

        cancelButton.setOnClickListener {
            if (listener != null) {
                listener?.onBusinessConfirmReview(false)
            }
        }
    }

    // Used for invoking the saved data from the parent Activity
    // This Fragment created before data filled by the user and we
    // can not use the arguments come to the fragment upon creation
    fun putArguments(params: HashMap<String, Any>) {
        this.params = params
        refreshUi()
    }

    // Ref: https://stackoverflow.com/questions/37949024/kotlin-typecastexception-null-cannot-be-cast-to-non-null-type-com-midsizemango
    private fun refreshUi() {
        // Business Details
        val name: String? = params?.get(Constants.BUSINESS_NAME) as? String
        val address: String? = params?.get(Constants.BUSINESS_ADDRESS) as? String
        val email: String? = params?.get(Constants.BUSINESS_EMAIL) as? String
        val phone: String? = params?.get(Constants.BUSINESS_PHONE) as? String
        val businessType: Int? = params?.get(Constants.BUSINESS_TYPE) as? Int
        val type: String? = LocationHelper.getTypeNameByPlaceType(businessType ?: 0)

        businessNameTV.text = name ?: ""
        businessAddressTV.text = address ?: ""
        BusinessEmailTV.text = email ?: ""
        BusinessPhoneTV.text = phone ?: ""
        BusinessTypeTV.text = type ?: ""

        // Opening Hours
//        val daysList: List<DayOpeningModel>? = params?.get(Constants.OPENING_HOURS) as? List<DayOpeningModel>
//        if (daysList != null) {
//            // Handling Monday
//            val monday: DayOpeningModel? = getDay("Monday", daysList)
//            if (monday == null || monday.isClosed != false) {
//                mondayClosed.visibility = View.VISIBLE
//                mondayHoursForm.visibility = View.GONE
//            } else {
//                mondayClosed.visibility = View.GONE
//                mondayHoursForm.visibility = View.VISIBLE
//
//                mondayFromHours.text = monday.frmHrs ?: "00"
//                mondayFromMinutes.text = monday.frmMin ?: "00"
//                mondayFromTypes.text = monday.frmTypeStr ?: "AM"
//
//                mondayTillHours.text = monday.tilHrs ?: "00"
//                mondayTillMinutes.text = monday.tilMin ?: "00"
//                mondayTillTypes.text = monday.tilTypeStr ?: "PM"
//            }
//
//            // Handling Tuesday
//            val tuesday: DayOpeningModel? = getDay("Tuesday", daysList)
//            if (tuesday == null || tuesday.isClosed != false) {
//                tuesdayClosed.visibility = View.VISIBLE
//                tuesdayHoursForm.visibility = View.GONE
//            } else {
//                tuesdayClosed.visibility = View.GONE
//                tuesdayHoursForm.visibility = View.VISIBLE
//
//                tuesdayFromHours.text = tuesday.frmHrs ?: "00"
//                tuesdayFromMinutes.text = tuesday.frmMin ?: "00"
//                tuesdayFromTypes.text = tuesday.frmTypeStr ?: "AM"
//
//                tuesdayTillHours.text = tuesday.tilHrs ?: "00"
//                tuesdayTillMinutes.text = tuesday.tilMin ?: "00"
//                tuesdayTillTypes.text = tuesday.tilTypeStr ?: "PM"
//            }
//
//            // Handling Wednesday
//            val wednesday: DayOpeningModel? = getDay("Wednesday", daysList)
//            if (wednesday == null || wednesday.isClosed != false) {
//                wednesdayClosed.visibility = View.VISIBLE
//                wednesdayHoursForm.visibility = View.GONE
//            } else {
//                wednesdayClosed.visibility = View.GONE
//                wednesdayHoursForm.visibility = View.VISIBLE
//
//                wednesdayFromHours.text = wednesday.frmHrs ?: "00"
//                wednesdayFromMinutes.text = wednesday.frmMin ?: "00"
//                wednesdayFromTypes.text = wednesday.frmTypeStr ?: "AM"
//
//                wednesdayTillHours.text = wednesday.tilHrs ?: "00"
//                wednesdayTillMinutes.text = wednesday.tilMin ?: "00"
//                wednesdayTillTypes.text = wednesday.tilTypeStr ?: "PM"
//            }
//
//            // Handling Thursday
//            val thursday: DayOpeningModel? = getDay("Thursday", daysList)
//            if (thursday == null || thursday.isClosed != false) {
//                thursdayClosed.visibility = View.VISIBLE
//                thursdayHoursForm.visibility = View.GONE
//            } else {
//                thursdayClosed.visibility = View.GONE
//                thursdayHoursForm.visibility = View.VISIBLE
//
//                thursdayFromHours.text = thursday.frmHrs ?: "00"
//                thursdayFromMinutes.text = thursday.frmMin ?: "00"
//                thursdayFromTypes.text = thursday.frmTypeStr ?: "AM"
//
//                thursdayTillHours.text = thursday.tilHrs ?: "00"
//                thursdayTillMinutes.text = thursday.tilMin ?: "00"
//                thursdayTillTypes.text = thursday.tilTypeStr ?: "PM"
//            }
//
//            // Handling Friday
//            val friday: DayOpeningModel? = getDay("Friday", daysList)
//            if (friday == null || friday.isClosed != false) {
//                fridayClosed.visibility = View.VISIBLE
//                fridayHoursForm.visibility = View.GONE
//            } else {
//                fridayClosed.visibility = View.GONE
//                fridayHoursForm.visibility = View.VISIBLE
//
//                fridayFromHours.text = friday.frmHrs ?: "00"
//                fridayFromMinutes.text = friday.frmMin ?: "00"
//                fridayFromTypes.text = friday.frmTypeStr ?: "AM"
//
//                fridayTillHours.text = friday.tilHrs ?: "00"
//                fridayTillMinutes.text = friday.tilMin ?: "00"
//                fridayTillTypes.text = friday.tilTypeStr ?: "PM"
//            }
//
//            // Handling Saturday
//            val saturday: DayOpeningModel? = getDay("Saturday", daysList)
//            if (saturday == null || saturday.isClosed != false) {
//                saturdayClosed.visibility = View.VISIBLE
//                saturdayHoursForm.visibility = View.GONE
//            } else {
//                saturdayClosed.visibility = View.GONE
//                saturdayHoursForm.visibility = View.VISIBLE
//
//                saturdayFromHours.text = saturday.frmHrs ?: "00"
//                saturdayFromMinutes.text = saturday.frmMin ?: "00"
//                saturdayFromTypes.text = saturday.frmTypeStr ?: "AM"
//
//                saturdayTillHours.text = saturday.tilHrs ?: "00"
//                saturdayTillMinutes.text = saturday.tilMin ?: "00"
//                saturdayTillTypes.text = saturday.tilTypeStr ?: "PM"
//            }
//
//            // Handling Sunday
//            val sunday: DayOpeningModel? = getDay("Sunday", daysList)
//            if (sunday == null || sunday.isClosed != false) {
//                sundayClosed.visibility = View.VISIBLE
//                sundayHoursForm.visibility = View.GONE
//            } else {
//                sundayClosed.visibility = View.GONE
//                sundayHoursForm.visibility = View.VISIBLE
//
//                sundayFromHours.text = sunday.frmHrs ?: "00"
//                sundayFromMinutes.text = sunday.frmMin ?: "00"
//                sundayFromTypes.text = sunday.frmTypeStr ?: "AM"
//
//                sundayTillHours.text = sunday.tilHrs ?: "00"
//                sundayTillMinutes.text = sunday.tilMin ?: "00"
//                sundayTillTypes.text = sunday.tilTypeStr ?: "PM"
//            }
//        }

        // Contacts List
        // Ref: https://stackoverflow.com/questions/37949024/kotlin-typecastexception-null-cannot-be-cast-to-non-null-type-com-midsizemango
        val contactsList: List<DecisionMakerModel>? =
            params?.get(Constants.DECISION_MAKERS) as? List<DecisionMakerModel>
        if (contactsList == null || contactsList.isEmpty()) {
            contactsContainer.visibility = View.GONE
        } else {
            contactsContainer.visibility = View.VISIBLE
            if (contactsContainer.childCount > 0) {
                contactsContainer.removeAllViews()
            }

            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            layoutParams.topMargin = resources.getDimensionPixelOffset(R.dimen.spacing_1x)
            layoutParams.bottomMargin = resources.getDimensionPixelOffset(R.dimen.spacing_1x)

            for (decisionMaker: DecisionMakerModel in contactsList) {

                val decisionMakerView = DecisionMakerDisplayView(context!!)
                decisionMakerView.setName(decisionMaker.name ?: "")
                decisionMakerView.setEmail(decisionMaker.email ?: "")
                decisionMakerView.setPhone(decisionMaker.phone ?: "")
                decisionMakerView.setComment(decisionMaker.comment ?: "")
                decisionMakerView.setIsDecisionMaker(decisionMaker.isDecisionMaker ?: 1)

                contactsContainer.addView(decisionMakerView, layoutParams)
            }
        }
    }

    private fun getDay(dayName: String, daysList: List<DayOpeningModel>): DayOpeningModel? {
        for (day: DayOpeningModel in daysList) {
            if (day.name?.equals(dayName, true) == true) {
                return day
            }
        }
        return null
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}