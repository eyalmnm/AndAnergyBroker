package com.usenergysolutions.energybroker.view.maps.dialogs

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import android.widget.ArrayAdapter.createFromResource
import com.nightonke.jellytogglebutton.JellyToggleButton
import com.nightonke.jellytogglebutton.State
import com.usenergysolutions.energybroker.R

class OpeningHoursDialog : DialogFragment() {
    private val TAG: String = "OpeningHoursDialog"

    interface OnOpeningHoursSetListener {
        fun onOpeningHours(daysAndHours: List<String>)
    }

    private var listener: OnOpeningHoursSetListener? = null

    // UI Components
    private lateinit var nonStopToggleButton: JellyToggleButton

    private lateinit var mondayOpeningData: RelativeLayout
    private lateinit var mondayClosed: Switch
    private lateinit var mondayHoursForm: LinearLayout
    private lateinit var mondayFromHours: EditText
    private lateinit var mondayFromMinutes: EditText
    private lateinit var mondayFromTypesSpinner: Spinner
    private lateinit var mondayTillHours: EditText
    private lateinit var mondayTillMinutes: EditText
    private lateinit var mondayTillTypesSpinner: Spinner

    private lateinit var tuesdayOpeningData: RelativeLayout
    private lateinit var tuesdayClosed: Switch
    private lateinit var tuesdayHoursForm: LinearLayout
    private lateinit var tuesdayFromHours: EditText
    private lateinit var tuesdayFromMinutes: EditText
    private lateinit var tuesdayFromTypesSpinner: Spinner
    private lateinit var tuesdayTillHours: EditText
    private lateinit var tuesdayTillMinutes: EditText
    private lateinit var tuesdayTillTypesSpinner: Spinner

    private lateinit var wednesdayOpeningData: RelativeLayout
    private lateinit var wednesdayClosed: Switch
    private lateinit var wednesdayHoursForm: LinearLayout
    private lateinit var wednesdayFromHours: EditText
    private lateinit var wednesdayFromMinutes: EditText
    private lateinit var wednesdayFromTypesSpinner: Spinner
    private lateinit var wednesdayTillHours: EditText
    private lateinit var wednesdayTillMinutes: EditText
    private lateinit var wednesdayTillTypesSpinner: Spinner

    private lateinit var thursdayOpeningData: RelativeLayout
    private lateinit var thursdayClosed: Switch
    private lateinit var thursdayHoursForm: LinearLayout
    private lateinit var thursdayFromHours: EditText
    private lateinit var thursdayFromMinutes: EditText
    private lateinit var thursdayFromTypesSpinner: Spinner
    private lateinit var thursdayTillHours: EditText
    private lateinit var thursdayTillMinutes: EditText
    private lateinit var thursdayTillTypesSpinner: Spinner

    private lateinit var fridayOpeningData: RelativeLayout
    private lateinit var fridayClosed: Switch
    private lateinit var fridayHoursForm: LinearLayout
    private lateinit var fridayFromHours: EditText
    private lateinit var fridayFromMinutes: EditText
    private lateinit var fridayFromTypesSpinner: Spinner
    private lateinit var fridayTillHours: EditText
    private lateinit var fridayTillMinutes: EditText
    private lateinit var fridayTillTypesSpinner: Spinner

    private lateinit var saturdayOpeningData: RelativeLayout
    private lateinit var saturdayClosed: Switch
    private lateinit var saturdayHoursForm: LinearLayout
    private lateinit var saturdayFromHours: EditText
    private lateinit var saturdayFromMinutes: EditText
    private lateinit var saturdayFromTypesSpinner: Spinner
    private lateinit var saturdayTillHours: EditText
    private lateinit var saturdayTillMinutes: EditText
    private lateinit var saturdayTillTypesSpinner: Spinner

    private lateinit var sundayOpeningData: RelativeLayout
    private lateinit var sundayClosed: Switch
    private lateinit var sundayHoursForm: LinearLayout
    private lateinit var sundayFromHours: EditText
    private lateinit var sundayFromMinutes: EditText
    private lateinit var sundayFromTypesSpinner: Spinner
    private lateinit var sundayTillHours: EditText
    private lateinit var sundayTillMinutes: EditText
    private lateinit var sundayTillTypesSpinner: Spinner

    private lateinit var cancelButton: TextView
    private lateinit var saveButton: TextView

    // UI Helpers
    private lateinit var hoursTypesArray: Array<String>
    private var adapter: ArrayAdapter<CharSequence>? = null


    // User's input
    private var isOpenNonStop: Boolean = false

    private var mondayIsClosed: Boolean = false
    private var mondayFromTypes: String = "AM"
    private var mondayTillTypes: String = "PM"

    private var tuesdayIsClosed: Boolean = false
    private var tuesdayFromType: String = "AM"
    private var tuesdayTillType: String = "PM"

    private var wednesdayIsClosed: Boolean = false
    private var wednesdayFromType: String = "AM"
    private var wednesdayTillType: String = "PM"

    private var thursdayIsClosed: Boolean = false
    private var thursdayFromType: String = "AM"
    private var thursdayTillType: String = "PM"

    private var fridayIsClosed: Boolean = false
    private var fridayFromType: String = "AM"
    private var fridayTillType: String = "PM"

    private var saturdayIsClosed: Boolean = false
    private var saturdayFromType: String = "AM"
    private var saturdayTillType: String = "PM"

    private var sundayIsClosed: Boolean = false
    private var sundayFromType: String = "AM"
    private var sundayTillType: String = "PM"


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnOpeningHoursSetListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false
        return inflater.inflate(R.layout.dialog_opening_hours, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init Helpers
        this.hoursTypesArray = resources.getStringArray(R.array.hours_types_array)

        // Bind UI Components
        nonStopToggleButton = view.findViewById(R.id.nonStopToggleButton)

        mondayOpeningData = view.findViewById(R.id.mondayOpeningData)
        mondayClosed = view.findViewById(R.id.mondayClosed)
        mondayHoursForm = view.findViewById(R.id.mondayHoursForm)
        mondayFromHours = view.findViewById(R.id.mondayFromHours)
        mondayFromMinutes = view.findViewById(R.id.mondayFromMinutes)
        mondayFromTypesSpinner = view.findViewById(R.id.mondayFromTypesSpinner)
        mondayTillHours = view.findViewById(R.id.mondayTillHours)
        mondayTillMinutes = view.findViewById(R.id.mondayTillMinutes)
        mondayTillTypesSpinner = view.findViewById(R.id.mondayTillTypesSpinner)

        tuesdayOpeningData = view.findViewById(R.id.tuesdayOpeningData)
        tuesdayClosed = view.findViewById(R.id.tuesdayClosed)
        tuesdayHoursForm = view.findViewById(R.id.tuesdayHoursForm)
        tuesdayFromHours = view.findViewById(R.id.tuesdayFromHours)
        tuesdayFromMinutes = view.findViewById(R.id.tuesdayFromMinutes)
        tuesdayFromTypesSpinner = view.findViewById(R.id.tuesdayFromTypesSpinner)
        tuesdayTillHours = view.findViewById(R.id.tuesdayTillHours)
        tuesdayTillMinutes = view.findViewById(R.id.tuesdayTillMinutes)
        tuesdayTillTypesSpinner = view.findViewById(R.id.tuesdayTillTypesSpinner)

        wednesdayOpeningData = view.findViewById(R.id.wednesdayOpeningData)
        wednesdayClosed = view.findViewById(R.id.wednesdayClosed)
        wednesdayHoursForm = view.findViewById(R.id.wednesdayHoursForm)
        wednesdayFromHours = view.findViewById(R.id.wednesdayFromHours)
        wednesdayFromMinutes = view.findViewById(R.id.wednesdayFromMinutes)
        wednesdayFromTypesSpinner = view.findViewById(R.id.wednesdayFromTypesSpinner)
        wednesdayTillHours = view.findViewById(R.id.wednesdayTillHours)
        wednesdayTillMinutes = view.findViewById(R.id.wednesdayTillMinutes)
        wednesdayTillTypesSpinner = view.findViewById(R.id.wednesdayTillTypesSpinner)

        thursdayOpeningData = view.findViewById(R.id.thursdayOpeningData)
        thursdayClosed = view.findViewById(R.id.thursdayClosed)
        thursdayHoursForm = view.findViewById(R.id.thursdayHoursForm)
        thursdayFromHours = view.findViewById(R.id.thursdayFromHours)
        thursdayFromMinutes = view.findViewById(R.id.thursdayFromMinutes)
        thursdayFromTypesSpinner = view.findViewById(R.id.thursdayFromTypesSpinner)
        thursdayTillHours = view.findViewById(R.id.thursdayTillHours)
        thursdayTillMinutes = view.findViewById(R.id.thursdayTillMinutes)
        thursdayTillTypesSpinner = view.findViewById(R.id.thursdayTillTypesSpinner)

        fridayOpeningData = view.findViewById(R.id.fridayOpeningData)
        fridayClosed = view.findViewById(R.id.fridayClosed)
        fridayHoursForm = view.findViewById(R.id.fridayHoursForm)
        fridayFromHours = view.findViewById(R.id.fridayFromHours)
        fridayFromMinutes = view.findViewById(R.id.fridayFromMinutes)
        fridayFromTypesSpinner = view.findViewById(R.id.fridayFromTypesSpinner)
        fridayTillHours = view.findViewById(R.id.fridayTillHours)
        fridayTillMinutes = view.findViewById(R.id.fridayTillMinutes)
        fridayTillTypesSpinner = view.findViewById(R.id.fridayTillTypesSpinner)

        saturdayOpeningData = view.findViewById(R.id.saturdayOpeningData)
        saturdayClosed = view.findViewById(R.id.saturdayClosed)
        saturdayHoursForm = view.findViewById(R.id.saturdayHoursForm)
        saturdayFromHours = view.findViewById(R.id.saturdayFromHours)
        saturdayFromMinutes = view.findViewById(R.id.saturdayFromMinutes)
        saturdayFromTypesSpinner = view.findViewById(R.id.saturdayFromTypesSpinner)
        saturdayTillHours = view.findViewById(R.id.saturdayTillHours)
        saturdayTillMinutes = view.findViewById(R.id.saturdayTillMinutes)
        saturdayTillTypesSpinner = view.findViewById(R.id.saturdayTillTypesSpinner)

        sundayOpeningData = view.findViewById(R.id.sundayOpeningData)
        sundayClosed = view.findViewById(R.id.sundayClosed)
        sundayHoursForm = view.findViewById(R.id.sundayHoursForm)
        sundayFromHours = view.findViewById(R.id.sundayFromHours)
        sundayFromMinutes = view.findViewById(R.id.sundayFromMinutes)
        sundayFromTypesSpinner = view.findViewById(R.id.sundayFromTypesSpinner)
        sundayTillHours = view.findViewById(R.id.sundayTillHours)
        sundayTillMinutes = view.findViewById(R.id.sundayTillMinutes)
        sundayTillTypesSpinner = view.findViewById(R.id.sundayTillTypesSpinner)

        cancelButton = view.findViewById(R.id.cancelButton)
        saveButton = view.findViewById(R.id.saveButton)

        adapter = createFromResource(context, R.array.hours_types_array, R.layout.add_place_spinner_item)

        // Add Listener to Non Stop Toggle Button
        nonStopToggleButton.onStateChangeListener =
            JellyToggleButton.OnStateChangeListener { process, state, jtb ->
                if (state!! == State.LEFT) {
                    isOpenNonStop = false
                    showDays(true)
                } else if (state == State.RIGHT) {
                    isOpenNonStop = true
                    showDays(false)
                }
            }

        //  Monday Components
        mondayClosed.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, "setOnCheckedChangeListener isChecked: $isChecked")
            mondayIsClosed = isChecked
            mondayHoursForm.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        mondayFromTypesSpinner.adapter = adapter
        mondayFromTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mondayFromTypes = hoursTypesArray[position]
            }
        }
        mondayFromTypesSpinner.setSelection(0)
        mondayFromTypes[0]

        mondayTillTypesSpinner.adapter = adapter
        mondayTillTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mondayTillTypes = hoursTypesArray[position]
            }
        }
        mondayTillTypesSpinner.setSelection(1)
        mondayTillTypes = hoursTypesArray[1]

        // Tuesday Components
        tuesdayClosed.setOnCheckedChangeListener { _, isChecked ->
            tuesdayIsClosed = isChecked
            tuesdayHoursForm.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        tuesdayFromTypesSpinner.adapter = adapter
        tuesdayFromTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tuesdayFromType = hoursTypesArray[position]
            }
        }
        tuesdayFromTypesSpinner.setSelection(0)
        tuesdayFromType = hoursTypesArray[0]

        tuesdayTillTypesSpinner.adapter = adapter
        tuesdayTillTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tuesdayTillType = hoursTypesArray[position]
            }
        }
        tuesdayTillTypesSpinner.setSelection(1)
        tuesdayTillType = hoursTypesArray[1]

        // Wednesday Components
        wednesdayClosed.setOnCheckedChangeListener { _, isChecked ->
            wednesdayIsClosed = isChecked
            wednesdayHoursForm.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        wednesdayFromTypesSpinner.adapter = adapter
        wednesdayFromTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                wednesdayFromType = hoursTypesArray[position]
            }
        }
        wednesdayFromTypesSpinner.setSelection(0)
        wednesdayFromType = hoursTypesArray[0]

        wednesdayTillTypesSpinner.adapter = adapter
        wednesdayTillTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                wednesdayTillType = hoursTypesArray[position]
            }
        }
        wednesdayTillTypesSpinner.setSelection(1)
        wednesdayTillType = hoursTypesArray[1]

        // Thursday Components
        thursdayClosed.setOnCheckedChangeListener { _, isChecked ->
            thursdayIsClosed = isChecked
            thursdayHoursForm.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        thursdayFromTypesSpinner.adapter = adapter
        thursdayFromTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                thursdayFromType = hoursTypesArray[position]
            }
        }
        thursdayFromTypesSpinner.setSelection(0)
        thursdayFromType = hoursTypesArray[0]

        thursdayTillTypesSpinner.adapter = adapter
        thursdayTillTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                thursdayTillType = hoursTypesArray[position]
            }
        }
        thursdayTillTypesSpinner.setSelection(1)
        thursdayTillType = hoursTypesArray[1]

        // Friday Components
        fridayClosed.setOnCheckedChangeListener { _, isChecked ->
            fridayIsClosed = isChecked
            fridayHoursForm.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        fridayFromTypesSpinner.adapter = adapter
        fridayFromTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fridayFromType = hoursTypesArray[position]
            }
        }
        fridayFromTypesSpinner.setSelection(0)
        fridayFromType = hoursTypesArray[0]

        fridayTillTypesSpinner.adapter = adapter
        fridayTillTypesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fridayTillType = hoursTypesArray[position]
            }
        }
        fridayTillTypesSpinner.setSelection(1)
        fridayTillType = hoursTypesArray[1]

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

        cancelButton.setOnClickListener { dismiss() }

        saveButton.setOnClickListener {
            saveData()
            dismiss()
        }
    }

    private fun showDays(show: Boolean) {
        mondayOpeningData.visibility = if (show) View.VISIBLE else View.GONE
        tuesdayOpeningData.visibility = if (show) View.VISIBLE else View.GONE
        wednesdayOpeningData.visibility = if (show) View.VISIBLE else View.GONE
        thursdayOpeningData.visibility = if (show) View.VISIBLE else View.GONE
        fridayOpeningData.visibility = if (show) View.VISIBLE else View.GONE
        saturdayOpeningData.visibility = if (show) View.VISIBLE else View.GONE
        sundayOpeningData.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun saveData() {
        if (listener != null) {
            val retArray: MutableList<String> = arrayListOf()
            if (isOpenNonStop) {
                retArray.add("Monday: 00:00 AM – 00:00 PM")
                retArray.add("Tuesday: 00:00 AM – 00:00 PM")
                retArray.add("Wednesday: 00:00 AM – 00:00 PM")
                retArray.add("Thursday: 00:00 AM – 00:00 PM")
                retArray.add("Friday: 00:00 AM – 00:00 PM")
                retArray.add("Saturday: 00:00 AM – 00:00 PM")
                retArray.add("Sunday: 00:00 AM – 00:00 PM")
            } else {
                val mondayString: String? = getMonday() ?: return
                val tuesdayString: String? = getTuesday() ?: return
                val wednesdayString: String? = getWednesday() ?: return
                val thursdayString: String? = getThursday() ?: return
                val fridayString: String? = getFriday() ?: return
                val saturdayString: String? = getSaturday() ?: return
                val sundayString: String? = getSunday() ?: return
                mondayString?.let { retArray.add(it) }
                tuesdayString?.let { retArray.add(it) }
                wednesdayString?.let { retArray.add(it) }
                thursdayString?.let { retArray.add(it) }
                fridayString?.let { retArray.add(it) }
                saturdayString?.let { retArray.add(it) }
                sundayString?.let { retArray.add(it) }
            }
            listener?.onOpeningHours(retArray)
        }
    }

    private fun getMonday(): String? {
        return getDayData(
            "Monday", mondayIsClosed, mondayFromHours, mondayFromMinutes, mondayFromTypes,
            mondayTillHours, mondayTillMinutes, mondayTillTypes
        )
    }

    private fun getTuesday(): String? {
        return getDayData(
            "Tuesday", tuesdayIsClosed, tuesdayFromHours, tuesdayFromMinutes, tuesdayFromType,
            tuesdayTillHours, tuesdayTillMinutes, tuesdayTillType
        )
    }

    private fun getWednesday(): String? {
        return getDayData(
            "Wednesday", wednesdayIsClosed, wednesdayFromHours, wednesdayFromMinutes,
            wednesdayFromType, wednesdayTillHours, wednesdayTillMinutes, wednesdayTillType
        )
    }

    private fun getThursday(): String? {
        return getDayData(
            "Thursday", tuesdayIsClosed, thursdayFromHours, thursdayFromMinutes, thursdayFromType,
            thursdayTillHours, thursdayTillMinutes, thursdayTillType
        )
    }

    private fun getFriday(): String? {
        return getDayData(
            "Friday", fridayIsClosed, fridayFromHours, fridayFromMinutes, fridayFromType,
            fridayTillHours, fridayTillMinutes, fridayTillType
        )
    }

    private fun getSaturday(): String? {
        return getDayData(
            "Saturday", saturdayIsClosed, saturdayFromHours, saturdayFromMinutes, saturdayFromType,
            saturdayTillHours, saturdayTillMinutes, saturdayTillType
        )
    }

    private fun getSunday(): String? {
        return getDayData(
            "Sunday", sundayIsClosed, sundayFromHours, sundayFromMinutes, sundayFromType,
            sundayTillHours, sundayTillMinutes, sundayTillType
        )
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
        return "$name: ${toTwoChars(frmHrs)}:${toTwoChars(frmMin)} $frmType - ${toTwoChars(tilHrs)}:${toTwoChars(tilMin)} $tilType"
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
        if (number == 0) return "00"
        else if (number < 10) return "0$number"
        else return "$number"
    }

    private fun showToast(msg: String): Unit {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}