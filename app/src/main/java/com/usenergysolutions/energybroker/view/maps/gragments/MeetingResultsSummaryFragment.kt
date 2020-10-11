package com.usenergysolutions.energybroker.view.maps.gragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import com.android.datetimepicker.date.DatePickerDialog
import com.android.datetimepicker.time.RadialPickerLayout
import com.android.datetimepicker.time.TimePickerDialog
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.model.ExtendedBusinessInfoModel
import com.usenergysolutions.energybroker.utils.CalendarUtils
import kotlinx.android.synthetic.main.fragment_meeting_results_summary.*
import java.text.SimpleDateFormat
import java.util.*


class MeetingResultsSummaryFragment : Fragment() {
    private val TAG: String = "MeetingResultsSumFrag"

    interface OnMeetingResultsSummarySetListener {
        fun onMeetingResultsSummarySet(params: HashMap<String, Any>)
    }

    private var listener: OnMeetingResultsSummarySetListener? = null

    // Listeners
    private lateinit var seeTheBillRadioGroupListener: RadioGroup.OnCheckedChangeListener
    private lateinit var meetingSummaryRadioGroupListener: RadioGroup.OnCheckedChangeListener
    private lateinit var declineReasonsRadioGroupListener: RadioGroup.OnCheckedChangeListener

    var meetingStatus: Constants.Companion.MeetingResultsStatus = Constants.Companion.MeetingResultsStatus.NO_MEETING

    // Data Holder
    private var params: HashMap<String, Any> = hashMapOf()
    private var contractExpirationDate: Long? = null
    private var comeAgainReminderDate: Long? = null
    private var underContractDate: Long? = null
    private var seeTheBill: Boolean? = null

    // UI Helpers
    //enum class DatePickerMode { NONE, CONTRACT, AGAIN, UNDER }
    enum class DatePickerMode { NONE, CONTRACT, AGAIN }

    private var datePickerMode: DatePickerMode = DatePickerMode.NONE

    // Static Creator
    companion object {

        fun newInstance(businessInfoModel: ExtendedBusinessInfoModel): MeetingResultsSummaryFragment {
            val args = Bundle()
            args.putParcelable(Constants.APP_DATA, businessInfoModel)
            val fragment = MeetingResultsSummaryFragment()
            fragment.arguments = args
            return fragment
        }

        fun getDateAsString(date: Long): String {
            val myFormat = "MMM dd yyyy hh:mm aa" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            return sdf.format(date)
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnMeetingResultsSummarySetListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.usenergysolutions.energybroker.R.layout.fragment_meeting_results_summary, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        seeTheBillRadioGroupListener = object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if (checkedId == R.id.didSeeTheBillRadioButton) {
                    seeTheBill = true
                } else if (checkedId == R.id.didNotSeeTheBillRadioButton) {
                    seeTheBill = false
                }
            }
        }
        seeTheBillRadioGroup.setOnCheckedChangeListener(seeTheBillRadioGroupListener)
        seeTheBillRadioGroup.clearCheck()

        meetingSummaryRadioGroupListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
            // reasonFormLayout.visibility = View.GONE
            if (checkedId == R.id.signedRadioButton && signedRadioButton.isChecked) {
                meetingStatus = Constants.Companion.MeetingResultsStatus.SIGNED
                datePickerMode = DatePickerMode.CONTRACT
                contractExpirationMeetingDate.visibility = View.VISIBLE
                contractExpirationMeetingDate.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        showDateTimePicker(DatePickerMode.CONTRACT)
                    }
                })
                showDateTimePicker(DatePickerMode.CONTRACT)
                contractExpirationMeetingDate.text = CalendarUtils.getCurrentLocalTimeString()
                nextMeetingDate.visibility = View.GONE
                nextMeetingDate.text = null
                comeAgainReminderDate = null
                declineReasonsRadioGroup.visibility = View.GONE
            } else if (checkedId == R.id.comeAgainRadioButton && comeAgainRadioButton.isChecked) {
                meetingStatus = Constants.Companion.MeetingResultsStatus.COME_AGAIN
                datePickerMode = DatePickerMode.AGAIN
                contractExpirationMeetingDate.visibility = View.GONE
                contractExpirationMeetingDate.text = null
                nextMeetingDate.visibility = View.VISIBLE
                nextMeetingDate.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        showDateTimePicker(DatePickerMode.AGAIN)
                    }
                })
                showDateTimePicker(DatePickerMode.AGAIN)
                nextMeetingDate.text = CalendarUtils.getCurrentLocalTimeString()
                contractExpirationDate = null
                declineReasonsRadioGroup.visibility = View.GONE
            } else if (checkedId == R.id.declinedRadioButton && declinedRadioButton.isChecked) {
                contractExpirationMeetingDate.visibility = View.GONE
                datePickerMode = DatePickerMode.NONE
                contractExpirationMeetingDate.text = null
                contractExpirationDate = null
                nextMeetingDate.visibility = View.GONE
                nextMeetingDate.text = null
                comeAgainReminderDate = null
                declineReasonsRadioGroup.visibility = View.VISIBLE
            }
        }
        meetingSummaryRadioGroup.setOnCheckedChangeListener(meetingSummaryRadioGroupListener)
        meetingSummaryRadioGroup.clearCheck()

        declineReasonsRadioGroupListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
            // reasonFormLayout.visibility = View.GONE
            underContractDateTV.visibility = View.GONE
            meetingStatus = Constants.Companion.MeetingResultsStatus.OTHER
            meetingResultsReason.text = null
            if (checkedId == R.id.underContractRadioButton && underContractRadioButton.isChecked) {
                meetingStatus = Constants.Companion.MeetingResultsStatus.UNDER_CONTRACT
//                underContractDateTV.visibility = View.VISIBLE
//                underContractDateTV.setOnClickListener { showDateTimePicker(DatePickerMode.UNDER) }
//                showDateTimePicker(DatePickerMode.UNDER)
            } else if (checkedId == R.id.notInterestedRadioButton && notInterestedRadioButton.isChecked) {
                meetingStatus = Constants.Companion.MeetingResultsStatus.NOT_INTERESTED
            } else if (checkedId == R.id.noCommentRadioButton && noCommentRadioButton.isChecked) {
                meetingStatus = Constants.Companion.MeetingResultsStatus.NO_COMMENTS
            } else if (checkedId == R.id.otherRadioButton && otherRadioButton.isChecked) {
                meetingStatus = Constants.Companion.MeetingResultsStatus.OTHER
                reasonFormLayout.visibility = View.VISIBLE
            } else if (checkedId == R.id.dontComeAgainRadioButton && dontComeAgainRadioButton.isChecked) {
                meetingStatus = Constants.Companion.MeetingResultsStatus.DO_NOT_COME_AGAIN
            }
        }
        declineReasonsRadioGroup.setOnCheckedChangeListener(declineReasonsRadioGroupListener)
        declineReasonsRadioGroup.clearCheck()

        savetBtn.setOnClickListener {
            if (meetingStatus == Constants.Companion.MeetingResultsStatus.NO_MEETING) {
                Toast.makeText(context, R.string.meeting_results_summary_empty_selection, Toast.LENGTH_LONG)
                    .show()
            } else if (seeTheBill == null) {
                Toast.makeText(context, R.string.meeting_results_summary_empty_see_the_bill, Toast.LENGTH_LONG)
                    .show()
            } else {
                if (listener != null) {
                    val params: HashMap<String, Any> = hashMapOf()
                    val reason: String = meetingResultsReason.text.toString()
                    params.put(Constants.MEETING_SEE_THE_BILL, seeTheBill!!)
                    params.put(Constants.MEETING_STATUS_PARAM, meetingStatus.id)
                    params.put(Constants.MEETING_REMINDER, " ")
                    if (meetingStatus == Constants.Companion.MeetingResultsStatus.SIGNED && contractExpirationDate != null) {
                        params.put(Constants.MEETING_REMINDER, contractExpirationDate!!)
                    }
                    if (meetingStatus == Constants.Companion.MeetingResultsStatus.COME_AGAIN && comeAgainReminderDate != null) {
                        params.put(Constants.MEETING_REMINDER, comeAgainReminderDate!!)
                    }
                    if (meetingStatus == Constants.Companion.MeetingResultsStatus.UNDER_CONTRACT && underContractDate != null) {
                        params.put(Constants.MEETING_REMINDER, underContractDate!!)
                    }
                    if (meetingStatus == Constants.Companion.MeetingResultsStatus.OTHER) {
                        if (reason.isNullOrEmpty()) {
                            Toast.makeText(context, R.string.meeting_results_summary_empty_comment, Toast.LENGTH_LONG)
                                .show()
                            return@setOnClickListener
                        }
                    }
                    params.put(Constants.MEETING_REASON, reason)
                    listener?.onMeetingResultsSummarySet(params)
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        params.clear()
    }


    // *************   DATE TIME PICKER SECTION   ***************

    private fun showDateTimePicker(pickerMode: DatePickerMode) {
        val tempCal = Calendar.getInstance()
        if (pickerMode == DatePickerMode.CONTRACT && contractExpirationDate != null) {
            tempCal.timeInMillis = contractExpirationDate!!
        } else if (pickerMode == DatePickerMode.AGAIN && comeAgainReminderDate != null) {
            tempCal.timeInMillis = comeAgainReminderDate!!
//        } else if (pickerMode == DatePickerMode.UNDER && underContractDate != null) {
//            tempCal.timeInMillis = underContractDate!!
        } else {
            tempCal.timeInMillis = CalendarUtils.getCurrentLocalTimeLong()
        }
        val year = tempCal.get(Calendar.YEAR)
        val month = tempCal.get(Calendar.MONTH)
        val day = tempCal.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog: DatePickerDialog = DatePickerDialog.newInstance(OnDateSet(pickerMode), year, month, day)
        val todayCalendar = Calendar.getInstance()
        todayCalendar.timeInMillis = System.currentTimeMillis() - 1000
        datePickerDialog.minDate = todayCalendar
        datePickerDialog.isCancelable = false
        (datePickerDialog as DialogFragment).show(fragmentManager, "datePicker")
    }

    private fun showTimePicker(pickerMode: DatePickerMode) {
        Log.d(TAG, "showTimePicker")
        val tempCal = Calendar.getInstance()
        val hourOfDay: Int = tempCal.get(Calendar.HOUR_OF_DAY)
        val minutes: Int = tempCal.get(Calendar.MINUTE)

        val timePickerDialog: TimePickerDialog =
            TimePickerDialog.newInstance(OnTimeSet(pickerMode), hourOfDay, minutes, true)
        timePickerDialog.isCancelable = false
        (timePickerDialog as DialogFragment).show(fragmentManager, "timePicker")
    }


    inner class OnDateSet(val pickerMode: DatePickerMode) : DatePickerDialog.OnDateSetListener {

        override fun onDateCanceled(dialog: DatePickerDialog?) {
            resetMeetingStatus()
        }

        override fun onDateSet(dialog: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(year, monthOfYear, dayOfMonth)
            if (pickerMode == DatePickerMode.CONTRACT) {
                contractExpirationDate = calendar.timeInMillis
                contractExpirationMeetingDate.text = getDateAsString(contractExpirationDate!!)
            } else if (pickerMode == DatePickerMode.AGAIN) {
                comeAgainReminderDate = calendar.timeInMillis
                nextMeetingDate.text = getDateAsString(comeAgainReminderDate!!)
//            } else if (pickerMode == DatePickerMode.UNDER) {
//                underContractDate = calendar.timeInMillis
//                underContractDateTV.text = getDateAsString(underContractDate!!)
            }
//            if (pickerMode != DatePickerMode.UNDER) {
            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    showTimePicker(pickerMode)
                }
            }, 100)
//            }
        }

    }

    private fun resetMeetingStatus() {
        meetingSummaryRadioGroup.clearCheck()
        declineReasonsRadioGroup.clearCheck()
        declineReasonsRadioGroup.visibility = View.GONE
        underContractDateTV.visibility = View.GONE
        comeAgainReminderDate = null
        nextMeetingDate.text = null
        nextMeetingDate.visibility = View.GONE
        contractExpirationDate = null
        contractExpirationMeetingDate.text = null
        contractExpirationMeetingDate.visibility = View.GONE
        meetingStatus = Constants.Companion.MeetingResultsStatus.NO_MEETING
        Toast.makeText(context, "Please choose an option", Toast.LENGTH_SHORT).show()
    }

    inner class OnTimeSet(val pickerMode: DatePickerMode) : TimePickerDialog.OnTimeSetListener {

        override fun onTimeCanceled(view: RadialPickerLayout?) {
            resetMeetingStatus()
        }

        override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int) {
            var calendar: Calendar = Calendar.getInstance()
            if (pickerMode == DatePickerMode.CONTRACT) {
                calendar.timeInMillis = contractExpirationDate!!
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                contractExpirationDate = calendar.timeInMillis
                contractExpirationMeetingDate.text = getDateAsString(contractExpirationDate!!)
            } else if (pickerMode == DatePickerMode.AGAIN) {
                calendar.timeInMillis = comeAgainReminderDate!!
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                comeAgainReminderDate = calendar.timeInMillis
                nextMeetingDate.text = getDateAsString(comeAgainReminderDate!!)
//            } else if (pickerMode == DatePickerMode.UNDER) {
//                calendar.timeInMillis = underContractDate!!
//                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
//                calendar.set(Calendar.MINUTE, minute)
//                underContractDate = calendar.timeInMillis
//                underContractDateTV.text = getDateAsString(underContractDate!!)
            }
        }

    }
}