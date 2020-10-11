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
import com.usenergysolutions.energybroker.model.MeetingReportModel
import com.usenergysolutions.energybroker.utils.CalendarUtils
import kotlinx.android.synthetic.main.fragment_meeting_results_summary.*
import java.text.SimpleDateFormat
import java.util.*

class MeetingEditSummaryFragment : Fragment() {
    private val TAG: String = "MeetingEditSummaryFrag"

    interface OnMeetingEditSummarySetListener {
        fun onMeetingEditSummarySet(params: HashMap<String, Any>)
    }

    private var listener: OnMeetingEditSummarySetListener? = null

    // User's Data
    private var meetingReportModel: MeetingReportModel? = null
    private var isLoading: Boolean = true

    // Listeners
    private lateinit var seeTheBillRadioGroupListener: RadioGroup.OnCheckedChangeListener
    private lateinit var meetingSummaryRadioGroupListener: RadioGroup.OnCheckedChangeListener
    private lateinit var declineReasonsRadioGroupListener: RadioGroup.OnCheckedChangeListener

    var meetingStatus: Constants.Companion.MeetingResultsStatus = Constants.Companion.MeetingResultsStatus.NO_MEETING

    // Data Holder
    private var contractExpirationDate: Long? = null
    private var comeAgainReminderDate: Long? = null
    private var underContractDate: Long? = null
    private var seeTheBill: Boolean? = null

    private var datePickerMode: MeetingResultsSummaryFragment.DatePickerMode =
        MeetingResultsSummaryFragment.DatePickerMode.NONE


    // Static Creator
    companion object {

        fun newInstance(meetingReportModel: MeetingReportModel?): MeetingEditSummaryFragment {
            val args = Bundle()
            args.putParcelable(Constants.APP_DATA, meetingReportModel)
            val fragment = MeetingEditSummaryFragment()
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
        listener = context as? OnMeetingEditSummarySetListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meeting_results_summary, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        seeTheBillRadioGroupListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.didSeeTheBillRadioButton) {
                seeTheBill = true
            } else if (checkedId == R.id.didNotSeeTheBillRadioButton) {
                seeTheBill = false
            }
        }
        seeTheBillRadioGroup.setOnCheckedChangeListener(seeTheBillRadioGroupListener)
        seeTheBillRadioGroup.clearCheck()

        meetingSummaryRadioGroupListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
            // reasonFormLayout.visibility = View.GONE
            if (checkedId == R.id.signedRadioButton && signedRadioButton.isChecked) {
                meetingStatus = Constants.Companion.MeetingResultsStatus.SIGNED
                datePickerMode = MeetingResultsSummaryFragment.DatePickerMode.CONTRACT
                contractExpirationMeetingDate.visibility = View.VISIBLE
                contractExpirationMeetingDate.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        showDateTimePicker(MeetingResultsSummaryFragment.DatePickerMode.CONTRACT)
                    }
                })
                showDateTimePicker(MeetingResultsSummaryFragment.DatePickerMode.CONTRACT)
                contractExpirationMeetingDate.text = CalendarUtils.getCurrentLocalTimeString()
                nextMeetingDate.visibility = View.GONE
                nextMeetingDate.text = null
                comeAgainReminderDate = null
                declineReasonsRadioGroup.visibility = View.GONE
            } else if (checkedId == R.id.comeAgainRadioButton && comeAgainRadioButton.isChecked) {
                meetingStatus = Constants.Companion.MeetingResultsStatus.COME_AGAIN
                datePickerMode = MeetingResultsSummaryFragment.DatePickerMode.AGAIN
                contractExpirationMeetingDate.visibility = View.GONE
                contractExpirationMeetingDate.text = null
                nextMeetingDate.visibility = View.VISIBLE
                nextMeetingDate.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        showDateTimePicker(MeetingResultsSummaryFragment.DatePickerMode.AGAIN)
                    }
                })
                showDateTimePicker(MeetingResultsSummaryFragment.DatePickerMode.AGAIN)
                nextMeetingDate.text = CalendarUtils.getCurrentLocalTimeString()
                contractExpirationDate = null
                declineReasonsRadioGroup.visibility = View.GONE
            } else if (checkedId == R.id.declinedRadioButton && declinedRadioButton.isChecked) {
                contractExpirationMeetingDate.visibility = View.GONE
                datePickerMode = MeetingResultsSummaryFragment.DatePickerMode.NONE
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
//            meetingResultsReason.text = null
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
                    if (contractExpirationDate == null && comeAgainReminderDate == null && underContractDate == null) {
                        params.put(Constants.MEETING_REMINDER, " ")
                    }
                    params.put(Constants.MEETING_REASON, reason)
                    listener?.onMeetingEditSummarySet(params)
                }
            }
        }

        meetingReportModel = arguments?.getParcelable(Constants.APP_DATA)
        if (meetingReportModel != null) {
            initData(meetingReportModel!!)
        }
    }

    private fun initData(meetingReportModel: MeetingReportModel) {
        seeTheBill = meetingReportModel.seeTheBill == 1
        if (seeTheBill == true) {
            didSeeTheBillRadioButton.isChecked = true
        } else {
            didNotSeeTheBillRadioButton.isChecked = true
        }

        val reason: String = meetingReportModel.note ?: ""
        meetingResultsReason.setText(reason)

        val meetingStatusId: Int? = meetingReportModel.meetingStatusId
        val reminder: String? = meetingReportModel.reminder
        val reminderMillis: Long? = if (reminder == null) {
            0
        } else {
            CalendarUtils.stringToDate(reminder)?.time
        }

        when (meetingStatusId) {

            Constants.Companion.MeetingResultsStatus.SIGNED.id -> {
                signedRadioButton.isChecked = true
                contractExpirationDate = reminderMillis
                contractExpirationMeetingDate.visibility = View.VISIBLE
                contractExpirationMeetingDate.text = reminder
                meetingStatus = Constants.Companion.MeetingResultsStatus.SIGNED
            }

            Constants.Companion.MeetingResultsStatus.COME_AGAIN.id -> {
                comeAgainRadioButton.isChecked = true
                comeAgainReminderDate = reminderMillis
                nextMeetingDate.visibility = View.VISIBLE
                nextMeetingDate.text = reminder
                meetingStatus = Constants.Companion.MeetingResultsStatus.COME_AGAIN
            }

            Constants.Companion.MeetingResultsStatus.UNDER_CONTRACT.id -> {
                underContractRadioButton.isChecked = true
                declinedRadioButton.isChecked = true
                meetingStatus = Constants.Companion.MeetingResultsStatus.UNDER_CONTRACT
            }

            Constants.Companion.MeetingResultsStatus.NOT_INTERESTED.id -> {
                notInterestedRadioButton.isChecked = true
                declinedRadioButton.isChecked = true
                meetingStatus = Constants.Companion.MeetingResultsStatus.NOT_INTERESTED
            }

            Constants.Companion.MeetingResultsStatus.OTHER.id -> {
                otherRadioButton.isChecked = true
                declinedRadioButton.isChecked = true
                meetingStatus = Constants.Companion.MeetingResultsStatus.OTHER
            }

            Constants.Companion.MeetingResultsStatus.DO_NOT_COME_AGAIN.id -> {
                dontComeAgainRadioButton.isChecked = true
                declinedRadioButton.isChecked = true
                meetingStatus = Constants.Companion.MeetingResultsStatus.DO_NOT_COME_AGAIN
            }
        }
        isLoading = false
    }


    // *************   DATE TIME PICKER SECTION   ***************

    private fun showDateTimePicker(pickerMode: MeetingResultsSummaryFragment.DatePickerMode) {
        if (isLoading) return
        val tempCal = Calendar.getInstance()
        if (pickerMode == MeetingResultsSummaryFragment.DatePickerMode.CONTRACT && contractExpirationDate != null) {
            tempCal.timeInMillis = contractExpirationDate!!
        } else if (pickerMode == MeetingResultsSummaryFragment.DatePickerMode.AGAIN && comeAgainReminderDate != null) {
            tempCal.timeInMillis = comeAgainReminderDate!!
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
        (datePickerDialog as DialogFragment).show(fragmentManager, "datePicker")
    }

    private fun showTimePicker(pickerMode: MeetingResultsSummaryFragment.DatePickerMode) {
        Log.d(TAG, "showTimePicker")
        val tempCal = Calendar.getInstance()
        val hourOfDay: Int = tempCal.get(Calendar.HOUR_OF_DAY)
        val minutes: Int = tempCal.get(Calendar.MINUTE)

        val timePickerDialog: TimePickerDialog =
            TimePickerDialog.newInstance(OnTimeSet(pickerMode), hourOfDay, minutes, true)
        (timePickerDialog as DialogFragment).show(fragmentManager, "timePicker")
    }


    inner class OnDateSet(val pickerMode: MeetingResultsSummaryFragment.DatePickerMode) :
        DatePickerDialog.OnDateSetListener {

        override fun onDateCanceled(dialog: DatePickerDialog?) {
            resetMeetingStatus()
        }

        override fun onDateSet(dialog: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(year, monthOfYear, dayOfMonth)
            if (pickerMode == MeetingResultsSummaryFragment.DatePickerMode.CONTRACT) {
                contractExpirationDate = calendar.timeInMillis
                contractExpirationMeetingDate.text =
                    MeetingResultsSummaryFragment.getDateAsString(contractExpirationDate!!)
            } else if (pickerMode == MeetingResultsSummaryFragment.DatePickerMode.AGAIN) {
                comeAgainReminderDate = calendar.timeInMillis
                nextMeetingDate.text = MeetingResultsSummaryFragment.getDateAsString(comeAgainReminderDate!!)
            }
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

    inner class OnTimeSet(val pickerMode: MeetingResultsSummaryFragment.DatePickerMode) :
        TimePickerDialog.OnTimeSetListener {

        override fun onTimeCanceled(view: RadialPickerLayout?) {
            resetMeetingStatus()
        }

        override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int) {
            var calendar: Calendar = Calendar.getInstance()
            if (pickerMode == MeetingResultsSummaryFragment.DatePickerMode.CONTRACT) {
                calendar.timeInMillis = contractExpirationDate!!
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                contractExpirationDate = calendar.timeInMillis
                contractExpirationMeetingDate.text =
                    MeetingResultsSummaryFragment.getDateAsString(contractExpirationDate!!)
            } else if (pickerMode == MeetingResultsSummaryFragment.DatePickerMode.AGAIN) {
                calendar.timeInMillis = comeAgainReminderDate!!
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                comeAgainReminderDate = calendar.timeInMillis
                nextMeetingDate.text = MeetingResultsSummaryFragment.getDateAsString(comeAgainReminderDate!!)
            }
        }

    }

}