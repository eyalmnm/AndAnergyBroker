package com.usenergysolutions.energybroker.view.maps.gragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.nightonke.jellytogglebutton.JellyToggleButton
import com.nightonke.jellytogglebutton.State
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.model.DayOpeningModel
import com.usenergysolutions.energybroker.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_working_time.*

class WorkingTimeFragment : AddBusinessBaseFragment() {
    private val TAG: String = "WorkingTimeFragment"

    interface OnWorkingHoursSetListener {
        fun onWorkingHours(params: List<DayOpeningModel>, strings: List<String>, isOpenNonStop: Boolean)
    }

    private var listener: OnWorkingHoursSetListener? = null

    // UI Helpers
    private lateinit var hoursTypesArray: Array<String>
    private var adapter: ArrayAdapter<CharSequence>? = null

    // Helpers
    private val daysName: Array<String> =
        arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    // User's input
    private var isOpenNonStop: Boolean = false

    private var mondayToFridayIsClosed: Boolean = false
    private var mondayToFridayFromTypes: String = "AM"
    private var mondayToFridayTillTypes: String = "PM"

    private var saturdayIsClosed: Boolean = false
    private var saturdayFromType: String = "AM"
    private var saturdayTillType: String = "PM"

    private var sundayIsClosed: Boolean = false
    private var sundayFromType: String = "AM"
    private var sundayTillType: String = "PM"

    // Static Creator
    companion object {

        fun newInstance(): WorkingTimeFragment {
            return WorkingTimeFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnWorkingHoursSetListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_working_time, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        // Init Helpers
        this.hoursTypesArray = resources.getStringArray(R.array.hours_types_array)

        adapter = ArrayAdapter.createFromResource(context!!, R.array.hours_types_array, R.layout.add_place_spinner_item)

        // Add Listener to Non Stop Toggle Button
        workingHoursNonStopToggleButton.onStateChangeListener =
            JellyToggleButton.OnStateChangeListener { process, state, jtb ->
                if (state!! == State.LEFT) {
                    isOpenNonStop = false
                    showDays(true)
                } else if (state == State.RIGHT) {
                    isOpenNonStop = true
                    hideKeyboard(workingHoursNonStopToggleButton)
                    showDays(false)
                }
            }

        //  Monday Components
        mondayToFridayClosed.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, "setOnCheckedChangeListener isChecked: $isChecked")
            mondayToFridayIsClosed = isChecked
            mondayToFridayHoursForm.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        mondayToFridayFromTypesSpinner.adapter = adapter
        mondayToFridayFromTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mondayToFridayFromTypes = hoursTypesArray[position]
            }
        }
        mondayToFridayFromTypesSpinner.setSelection(0)
        mondayToFridayFromTypes[0]

        mondayToFridayTillTypesSpinner.adapter = adapter
        mondayToFridayTillTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mondayToFridayTillTypes = hoursTypesArray[position]
            }
        }
        mondayToFridayTillTypesSpinner.setSelection(1)
        mondayToFridayTillTypes = hoursTypesArray[1]

        // Saturday Components
        saturdayClosed.setOnCheckedChangeListener { _, isChecked ->
            saturdayIsClosed = isChecked
            saturdayHoursForm.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        saturdayFromTypesSpinner.adapter = adapter
        saturdayFromTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                saturdayFromType = hoursTypesArray[position]
            }
        }
        saturdayFromTypesSpinner.setSelection(0)
        saturdayFromType = hoursTypesArray[0]

        saturdayTillTypesSpinner.adapter = adapter
        saturdayTillTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                saturdayTillType = hoursTypesArray[position]
            }
        }
        saturdayTillTypesSpinner.setSelection(1)
        saturdayTillType = hoursTypesArray[1]

        // Sunday Components
        sundayClosed.setOnCheckedChangeListener { _, isChecked ->
            sundayIsClosed = isChecked
            sundayHoursForm.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        sundayFromTypesSpinner.adapter = adapter
        sundayFromTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sundayFromType = hoursTypesArray[position]
            }
        }
        sundayFromTypesSpinner.setSelection(0)
        sundayFromType = hoursTypesArray[1]

        sundayTillTypesSpinner.adapter = adapter
        sundayTillTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sundayTillType = hoursTypesArray[position]
            }
        }
        sundayTillTypesSpinner.setSelection(1)
        sundayTillType = hoursTypesArray[1]


        nextButton.setOnClickListener {
            saveOpeningData()
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun saveOpeningData() {
        if (listener != null) {
            val dayList: MutableList<DayOpeningModel> = arrayListOf()
            if (isOpenNonStop) {
                for (i in 0 until daysName.size) {
                    dayList.add(createDefaultDay(daysName[i]))
                }
            } else {
                // Monday
                val monday = getMonday() ?: return
                dayList.add(monday)

                // Tuesday
                val tuesday = getTuesday() ?: return
                dayList.add(tuesday)

                val wednesday = getWednesday() ?: return
                dayList.add(wednesday)

                // Thursday
                val thursday = getThursday() ?: return
                dayList.add(thursday)

                // Friday
                val friday = getFriday() ?: return
                dayList.add(friday)

                // Saturday
                val saturday = getSaturday() ?: return
                dayList.add(saturday)

                // Sunday
                val sunday = getSunday() ?: return
                dayList.add(sunday)
            }

            listener?.onWorkingHours(dayList, getOpeningDataAsStringArray(), isOpenNonStop)
        }
    }

    private fun createDefaultDay(name: String): DayOpeningModel {
        val day = DayOpeningModel()
        day.name = name
        day.isClosed = false
        day.frmHrs = "00"
        day.frmMin = "00"
        day.frmTypeStr = "AM"
        day.tilHrs = "00"
        day.tilMin = "00"
        day.tilTypeStr = "PM"
        return day
    }

    private fun showDays(show: Boolean) {
        mondayToFridayOpeningData.visibility = if (show) View.VISIBLE else View.GONE
        saturdayOpeningData.visibility = if (show) View.VISIBLE else View.GONE
        sundayOpeningData.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun getOpeningDataAsStringArray(): MutableList<String> {
        val retArray: MutableList<String> = arrayListOf()
        if (isOpenNonStop) {
            retArray.add("Monday: 00:00 AM  00:00 PM")
            retArray.add("Tuesday: 00:00 AM  00:00 PM")
            retArray.add("Wednesday: 00:00 AM  00:00 PM")
            retArray.add("Thursday: 00:00 AM  00:00 PM")
            retArray.add("Friday: 00:00 AM  00:00 PM")
            retArray.add("Saturday: 00:00 AM  00:00 PM")
            retArray.add("Sunday: 00:00 AM  00:00 PM")
        } else {
            val mondayString: String? = getMondayStr()
            val tuesdayString: String? = getTuesdayStr()
            val wednesdayString: String? = getWednesdayStr()
            val thursdayString: String? = getThursdayStr()
            val fridayString: String? = getFridayStr()
            val saturdayString: String? = getSaturdayStr()
            val sundayString: String? = getSundayStr()
            mondayString?.let { retArray.add(it) }
            tuesdayString?.let { retArray.add(it) }
            wednesdayString?.let { retArray.add(it) }
            thursdayString?.let { retArray.add(it) }
            fridayString?.let { retArray.add(it) }
            saturdayString?.let { retArray.add(it) }
            sundayString?.let { retArray.add(it) }
        }
        return retArray
    }

    private fun getMondayStr(): String? {
        return getDayData(
            "Monday",
            mondayToFridayIsClosed,
            mondayToFridayFromHours,
            mondayToFridayFromMinutes,
            mondayToFridayFromTypes,
            mondayToFridayTillHours,
            mondayToFridayTillMinutes,
            mondayToFridayTillTypes
        )
    }

    private fun getMonday(): DayOpeningModel? {
        val day = DayOpeningModel()
        day.name = "Monday"
        day.isClosed = mondayToFridayIsClosed
        day.frmHrs = mondayToFridayFromHours.text.toString()
        if (mondayToFridayFromMinutes.text.isNullOrEmpty()) mondayToFridayFromMinutes.setText("00")
        day.frmMin = mondayToFridayFromMinutes.text.toString()
        day.frmTypeStr = mondayToFridayFromTypes
        day.tilHrs = mondayToFridayTillHours.text.toString()
        if (mondayToFridayTillMinutes.text.isNullOrEmpty()) mondayToFridayTillMinutes.setText("00")
        day.tilMin = mondayToFridayTillMinutes.text.toString()
        day.tilTypeStr = mondayToFridayTillTypes
        return validateDay(day)
    }

    private fun getTuesdayStr(): String? {
        return getDayData(
            "Tuesday",
            mondayToFridayIsClosed,
            mondayToFridayFromHours,
            mondayToFridayFromMinutes,
            mondayToFridayFromTypes,
            mondayToFridayTillHours,
            mondayToFridayTillMinutes,
            mondayToFridayTillTypes
        )
    }

    private fun getTuesday(): DayOpeningModel? {
        val day = DayOpeningModel()
        day.name = "Tuesday"
        day.isClosed = mondayToFridayIsClosed
        day.frmHrs = mondayToFridayFromHours.text.toString()
        if (mondayToFridayFromMinutes.text.isNullOrEmpty()) mondayToFridayFromMinutes.setText("00")
        day.frmMin = mondayToFridayFromMinutes.text.toString()
        day.frmTypeStr = mondayToFridayFromTypes
        day.tilHrs = mondayToFridayTillHours.text.toString()
        if (mondayToFridayTillMinutes.text.isNullOrEmpty()) mondayToFridayTillMinutes.setText("00")
        day.tilMin = mondayToFridayTillMinutes.text.toString()
        day.tilTypeStr = mondayToFridayTillTypes
        return validateDay(day)
    }


    private fun getWednesdayStr(): String? {
        return getDayData(
            "Wednesday", mondayToFridayIsClosed, mondayToFridayFromHours, mondayToFridayFromMinutes,
            mondayToFridayFromTypes, mondayToFridayTillHours, mondayToFridayTillMinutes, mondayToFridayTillTypes
        )
    }

    private fun getWednesday(): DayOpeningModel? {
        val day = DayOpeningModel()
        day.name = "Wednesday"
        day.isClosed = mondayToFridayIsClosed
        day.frmHrs = mondayToFridayFromHours.text.toString()
        if (mondayToFridayFromMinutes.text.isNullOrEmpty()) mondayToFridayFromMinutes.setText("00")
        day.frmMin = mondayToFridayFromMinutes.text.toString()
        day.frmTypeStr = mondayToFridayFromTypes
        day.tilHrs = mondayToFridayTillHours.text.toString()
        if (mondayToFridayTillMinutes.text.isNullOrEmpty()) mondayToFridayTillMinutes.setText("00")
        day.tilMin = mondayToFridayTillMinutes.text.toString()
        day.tilTypeStr = mondayToFridayTillTypes
        return validateDay(day)
    }

    private fun getThursdayStr(): String? {
        return getDayData(
            "Thursday",
            mondayToFridayIsClosed,
            mondayToFridayFromHours,
            mondayToFridayFromMinutes,
            mondayToFridayFromTypes,
            mondayToFridayTillHours,
            mondayToFridayTillMinutes,
            mondayToFridayTillTypes
        )
    }

    private fun getThursday(): DayOpeningModel? {
        val day = DayOpeningModel()
        day.name = "Thursday"
        day.isClosed = mondayToFridayIsClosed
        day.frmHrs = mondayToFridayFromHours.text.toString()
        if (mondayToFridayFromMinutes.text.isNullOrEmpty()) mondayToFridayFromMinutes.setText("00")
        day.frmMin = mondayToFridayFromMinutes.text.toString()
        day.frmTypeStr = mondayToFridayFromTypes
        day.tilHrs = mondayToFridayTillHours.text.toString()
        if (mondayToFridayTillMinutes.text.isNullOrEmpty()) mondayToFridayTillMinutes.setText("00")
        day.tilMin = mondayToFridayTillMinutes.text.toString()
        day.tilTypeStr = mondayToFridayTillTypes
        return validateDay(day)
    }

    private fun getFridayStr(): String? {
        return getDayData(
            "Friday",
            mondayToFridayIsClosed,
            mondayToFridayFromHours,
            mondayToFridayFromMinutes,
            mondayToFridayFromTypes,
            mondayToFridayTillHours,
            mondayToFridayTillMinutes,
            mondayToFridayTillTypes
        )
    }

    private fun getFriday(): DayOpeningModel? {
        val day = DayOpeningModel()
        day.name = "Friday"
        day.isClosed = mondayToFridayIsClosed
        day.frmHrs = mondayToFridayFromHours.text.toString()
        if (mondayToFridayFromMinutes.text.isNullOrEmpty()) mondayToFridayFromMinutes.setText("00")
        day.frmMin = mondayToFridayFromMinutes.text.toString()
        day.frmTypeStr = mondayToFridayFromTypes
        day.tilHrs = mondayToFridayTillHours.text.toString()
        if (mondayToFridayTillMinutes.text.isNullOrEmpty()) mondayToFridayTillMinutes.setText("00")
        day.tilMin = mondayToFridayTillMinutes.text.toString()
        day.tilTypeStr = mondayToFridayTillTypes
        return validateDay(day)
    }

    private fun getSaturdayStr(): String? {
        return getDayData(
            "Saturday", saturdayIsClosed, saturdayFromHours, saturdayFromMinutes, saturdayFromType,
            saturdayTillHours, saturdayTillMinutes, saturdayTillType
        )
    }

    private fun getSaturday(): DayOpeningModel? {
        val day = DayOpeningModel()
        day.name = "Saturday"
        day.isClosed = saturdayIsClosed
        day.frmHrs = saturdayFromHours.text.toString()
        if (saturdayFromMinutes.text.isNullOrEmpty()) saturdayFromMinutes.setText("00")
        day.frmMin = saturdayFromMinutes.text.toString()
        day.frmTypeStr = saturdayFromType
        day.tilHrs = saturdayTillHours.text.toString()
        if (saturdayTillMinutes.text.isNullOrEmpty()) saturdayTillMinutes.setText("00")
        day.tilMin = saturdayTillMinutes.text.toString()
        day.tilTypeStr = saturdayTillType
        return validateDay(day)
    }

    private fun getSundayStr(): String? {
        return getDayData(
            "Sunday", sundayIsClosed, sundayFromHours, sundayFromMinutes, sundayFromType,
            sundayTillHours, sundayTillMinutes, sundayTillType
        )
    }

    private fun getSunday(): DayOpeningModel? {
        val day = DayOpeningModel()
        day.name = "Sunday"
        day.isClosed = sundayIsClosed
        day.frmHrs = sundayFromHours.text.toString()
        if (sundayFromMinutes.text.isNullOrEmpty()) sundayFromMinutes.setText("00")
        day.frmMin = sundayFromMinutes.text.toString()
        day.frmTypeStr = sundayFromType
        day.tilHrs = sundayTillHours.text.toString()
        if (sundayTillMinutes.text.isNullOrEmpty()) sundayTillMinutes.setText("00")
        day.tilMin = sundayTillMinutes.text.toString()
        day.tilTypeStr = sundayTillType
        return validateDay(day)
    }

    private fun validateDay(day: DayOpeningModel): DayOpeningModel? {
        if (day.isClosed!!) return day

        if (day.frmHrs.isNullOrEmpty()) {
            showToast("In ${day.name} from hour is missing")
            return null
        }
        val frmHrs: Int = day.frmHrs?.toInt() ?: -1
        if (!isValidHours(frmHrs)) {
            showToast("In ${day.name} from invalid hour $frmHrs")
            return null
        }
        day.frmHrs = toTwoChars(day.frmHrs!!.toInt())

        if (day.frmMin.isNullOrEmpty()) {
            showToast("In ${day.name} from minutes is missing")
            return null
        }
        val frmMin: Int = day.frmMin?.toInt() ?: -1
        if (!isValidMinutes(frmMin)) {
            showToast("In ${day.name} from invalid minutes $frmMin")
            return null
        }
        day.frmMin = toTwoChars(day.frmMin!!.toInt())

        if (day.tilHrs.isNullOrEmpty()) {
            showToast("In ${day.name} till hour is missing")
            return null
        }
        val tilHrs: Int = day.tilHrs?.toInt() ?: -1
        if (!isValidHours(tilHrs)) {
            showToast("In ${day.name} till invalid hour $tilHrs")
            return null
        }
        day.tilHrs = toTwoChars(day.tilHrs!!.toInt())

        if (day.tilMin.isNullOrEmpty()) {
            showToast("In ${day.name} till minutes is missing")
            return null
        }
        val tilMin: Int = day.tilMin?.toInt() ?: -1
        if (!isValidMinutes(tilMin)) {
            showToast("In ${day.name} till invalid minutes $tilMin")
            return null
        }
        day.tilMin = toTwoChars(day.tilMin!!.toInt())

        return day
    }

    private fun getDayData(
        name: String, isClosed: Boolean, frmHrsEt: EditText, frmMinEt: EditText, frmTypeStr: String,
        tilHrsEt: EditText, tilMinEt: EditText, tilTypeStr: String
    ): String? {
        if (isClosed) {
            return "$name: Closed"
        } else {
            val frmHrs: Int = getIntValue(frmHrsEt)
            if (!isValidHours(frmHrs)) {
                showToast("In $name from invalid hour $frmHrs")
                return null
            }
            val frmMin: Int = getIntValue(frmMinEt)
            if (!isValidMinutes(frmMin)) {
                showToast("In $name from invalid minutes $frmMin")
                return null
            }
            val tilHrs: Int = getIntValue(tilHrsEt)
            if (!isValidHours(tilHrs)) {
                showToast("In $name till invalid hour $tilHrs")
                return null
            }
            val tilMin: Int = getIntValue(tilMinEt)
            if (!isValidMinutes(tilMin)) {
                showToast("In $name till invalid minutes $tilMin")
                return null
            }
            return generateString(name, frmHrs, frmMin, frmTypeStr, tilHrs, tilMin, tilTypeStr)
        }
    }

    private fun generateString(
        name: String, frmHrs: Int, frmMin: Int, frmType: String, tilHrs: Int,
        tilMin: Int, tilType: String
    ): String {
        return "$name: ${toTwoChars(frmHrs)}:${toTwoChars(frmMin)} $frmType  ${toTwoChars(tilHrs)}:${toTwoChars(tilMin)} $tilType"
    }

    private fun getIntValue(editText: EditText): Int {
        var text: String = editText.text.toString()
        if (text.isEmpty()) {
            text = "0"
        }
        return text.toInt()
    }

    private fun isValidHours(hours: Int): Boolean {
        return hours in 0..12
    }

    private fun isValidMinutes(minutes: Int): Boolean {
        return minutes in 0..59
    }

    private fun toTwoChars(number: Int): String {
        return StringUtils.convertToTwoDigits(number)
    }

    private fun showToast(msg: String): Unit {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    override fun saveData() {
        saveOpeningData()
    }

}