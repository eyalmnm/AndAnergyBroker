package com.usenergysolutions.energybroker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.model.DecisionMakerModel
import com.usenergysolutions.energybroker.model.MeetingReportModel
import com.usenergysolutions.energybroker.utils.CalendarUtils

class AddedMeetingView(var listener: OnMeetingViewClickListener?, context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs), View.OnClickListener {
    private val TAG: String = "AddedMeetingView"

    interface OnMeetingViewClickListener {
        fun onMeetingViewClick(meetingReportModel: MeetingReportModel?)
    }

    private var meetingReportModel: MeetingReportModel? = null

    // UI Components
    private var layout: View = View.inflate(context, R.layout.view_added_meeting, null)
    private var businessNameTextView: TextView? = null
    private var businessAddressTextView: TextView? = null
    private var decisionMakersTitleTextView: TextView? = null
    private var decisionMakersContainer: LinearLayout? = null
    private var statusNameTextView: TextView? = null
    private var reminderTextView: TextView? = null
    private var seeTheBillAnswerTextView: TextView? = null
    private var creationTimeTextView: TextView? = null
    private var commentTitleTextView: TextView? = null
    private var commentTextTextView: TextView? = null

    private val layoutParams: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    init {
        setBackgroundColor(resources.getColor(android.R.color.transparent, null))

        // Bind UI Components
        businessNameTextView = layout.findViewById(R.id.businessNameTextView)
        businessAddressTextView = layout.findViewById(R.id.businessAddressTextView)
        decisionMakersTitleTextView = layout.findViewById(R.id.decisionMakersTitleTextView)
        decisionMakersContainer = layout.findViewById(R.id.decisionMakersContainer)
        statusNameTextView = layout.findViewById(R.id.statusNameTextView)
        reminderTextView = layout.findViewById(R.id.reminderTextView)
        seeTheBillAnswerTextView = layout.findViewById(R.id.seeTheBillAnswerTextView)
        creationTimeTextView = layout.findViewById(R.id.creationTimeTextView)
        commentTitleTextView = layout.findViewById(R.id.commentTitleTextView)
        commentTextTextView = layout.findViewById(R.id.commentTextTextView)

        addView(layout, layoutParams)

        layout.setOnClickListener(this)
    }

    fun setData(meetingReportModel: MeetingReportModel?) {
        this.meetingReportModel = meetingReportModel

        businessNameTextView?.text = meetingReportModel?.getBusinessName() ?: ""
        businessAddressTextView?.text = meetingReportModel?.getBusinessAddress() ?: ""
        statusNameTextView?.text = meetingReportModel?.meetingStatus ?: ""
        val reminder: String? = meetingReportModel?.reminder
        var lastDate: String? = meetingReportModel?.createdAt
        if (!reminder.isNullOrEmpty() && !reminder.contains("0000-00", true)) {
            reminderTextView?.text = CalendarUtils.convertTo12H(reminder)
        }
        if (!meetingReportModel?.updatedAt.isNullOrEmpty() && !meetingReportModel?.updatedAt?.contains(
                "0000-00",
                true
            )!!
        ) {
            lastDate = meetingReportModel.updatedAt
        }
        if (!lastDate.isNullOrEmpty() && !lastDate.contains("0000-00", true)) {
            creationTimeTextView?.text = CalendarUtils.convertTo12H(lastDate)
        }
        seeTheBillAnswerTextView?.text = if (meetingReportModel?.seeTheBill == 1) "Yes" else "No"

        decisionMakersContainer?.removeAllViews()
        decisionMakersTitleTextView?.visibility = View.GONE
        if (!meetingReportModel?.getDecisionMakersList().isNullOrEmpty()) {
            decisionMakersTitleTextView?.visibility = View.VISIBLE
            val contactLayoutParams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val margin: Int = resources.getDimensionPixelSize(R.dimen.spacing_1x)
            contactLayoutParams.setMargins(0, margin, 0, margin)
            for (decisionMaker: DecisionMakerModel in meetingReportModel?.getDecisionMakersList()!!) {
                val contactView: AddedMeetingContactView = AddedMeetingContactView(context)
                contactView.setData(decisionMaker.name, decisionMaker.phone, decisionMaker.email, decisionMaker.comment)
                decisionMakersContainer?.addView(contactView, contactLayoutParams)
            }
        }

        if (meetingReportModel?.note.isNullOrEmpty()) {
            commentTitleTextView?.visibility = View.GONE
            commentTextTextView?.visibility = View.GONE
        } else {
            commentTextTextView?.text = meetingReportModel?.note
        }
    }

    override fun onClick(v: View?) {
        if (listener != null) {
            listener?.onMeetingViewClick(meetingReportModel)
        }
    }
}