package com.usenergysolutions.energybroker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.usenergysolutions.energybroker.R
import kotlinx.android.synthetic.main.view_decision_maker_dispaly.view.*

class DecisionMakerDisplayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    // UI Components
    private var layout: View = View.inflate(context, R.layout.view_decision_maker_dispaly, null)

    init {
        setBackgroundColor(resources.getColor(android.R.color.transparent, null))
        addView(layout)
    }

    fun setName(name: String) {
        meetingResultsContactNameTv.text = name
    }

    fun getName(): String {
        return meetingResultsContactNameTv.text.toString()
    }

    fun setEmail(email: String) {
        meetingResultsContactEmailTv.text = email
    }

    fun getEmail(): String {
        return meetingResultsContactEmailTv.text.toString()
    }

    fun setPhone(phone: String) {
        meetingResultsContactPhoneTv.text = phone
    }

    fun getPhone(): String {
        return meetingResultsContactPhoneTv.text.toString()
    }

    fun setComment(comment: String) {
        if (comment.isNullOrEmpty()) {
            meetingResultsContactCommentTv.text = ""
            meetingResultsContactCommentTvLayout.visibility = View.GONE
        } else {
            meetingResultsContactCommentTv.text = comment
            meetingResultsContactCommentTvLayout.visibility = View.VISIBLE
        }
    }

    fun getComment(): String {
        return meetingResultsContactCommentTv.text.toString()
    }

    fun setIsDecisionMaker(isDecisionMaker: Int) {
        meetingResultsIsDecisionMakerToggleButton.isChecked = isDecisionMaker == 0
    }

    fun isDecisionMaker(): Boolean {
        return !meetingResultsIsDecisionMakerToggleButton.isChecked
    }
}