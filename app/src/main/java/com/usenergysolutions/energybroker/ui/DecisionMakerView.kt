package com.usenergysolutions.energybroker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.model.DecisionMakerModel
import kotlinx.android.synthetic.main.view_decision_maker.view.*

//class DecisionMakerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
//    LinearLayout(context, attrs) {


class DecisionMakerView(
    context: Context,
    var listener: OnClickListener?,
    var index: Int,
    attrs: AttributeSet? = null
) :
    LinearLayout(context, attrs) {
    private val TAG: String = "MeetingDecisionMakerView"

    private var id: Int? = null
    private var isDecisionMaker: Int = 1

    private var isLoading: Boolean = false

    interface OnClickListener {
        fun onClick(parentView: View, index: Int)
        fun notDecisionMaker(index: Int)
    }

    // UI Components
    private var layout: View = View.inflate(context, R.layout.view_decision_maker, null)
    private var meetingResultsAddDecisionMakerTextView: TextView? = null
    private var meetingResultsAddDecisionMakerIcon: ImageView? = null
    private var decisionMakerIsDecisionMakerToggleButton: ToggleButton? = null

    private val layoutParams: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    init {
        setBackgroundColor(resources.getColor(android.R.color.transparent, null))

        // Bind UI Components
        meetingResultsAddDecisionMakerTextView = layout.findViewById(R.id.meetingResultsAddDecisionMakerTextView)
        meetingResultsAddDecisionMakerIcon = layout.findViewById(R.id.meetingResultsAddDecisionMakerIcon)
        decisionMakerIsDecisionMakerToggleButton = layout.findViewById(R.id.decisionMakerIsDecisionMakerToggleButton)


        meetingResultsAddDecisionMakerTextView?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                meetingResultsAddDecisionMakerIcon?.performClick()
            }
        })

        meetingResultsAddDecisionMakerIcon?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (listener != null) {
                    listener?.onClick(this@DecisionMakerView, index)
                }
            }
        })

        decisionMakerIsDecisionMakerToggleButton?.setOnCheckedChangeListener { _, isChecked ->
            isDecisionMaker = if (isChecked) 0 else 1
            if (isLoading) return@setOnCheckedChangeListener
            if (isChecked) {
                if (listener != null) {
                    listener?.notDecisionMaker(index)
                }
            }
        }

        addView(layout, layoutParams)
    }

    fun changeIcon(addIcon: Boolean, index: Int) {
        this.index = index
        if (addIcon) {
            meetingResultsAddDecisionMakerTextView?.text = context.resources.getText(R.string.add_new_decision_maker)
            meetingResultsAddDecisionMakerIcon?.setImageResource(R.drawable.ic_add_white)
        } else {
            meetingResultsAddDecisionMakerTextView?.text = context.resources.getText(R.string.remove_decision_maker)
            meetingResultsAddDecisionMakerIcon?.setImageResource(R.drawable.ic_remove_circle_outline_white_24dp)
        }
    }

    fun setDecisionMaker(model: DecisionMakerModel) {
        isLoading = true
        isDecisionMaker = model.isDecisionMaker!!
        decisionMakerIsDecisionMakerToggleButton?.isChecked = isDecisionMaker == 0
        meetingResultsContactNameET.setText(model.name)
        meetingResultsContactEmailET.setText(model.email)
        meetingResultsContactPhoneET.setText(model.phone)
        meetingResultsContactCommentET.setText(model.comment)
        id = model.id
        isLoading = false
    }

    fun getDecisionMaker(): DecisionMakerModel {

        val isDecisionMaker = isDecisionMaker
        val name = meetingResultsContactNameET.text.toString()
        val email = meetingResultsContactEmailET.text.toString()
        val phone = meetingResultsContactPhoneET.text.toString()
        val comment = meetingResultsContactCommentET.text.toString()

        return DecisionMakerModel(
            id,
            name,
            email,
            phone,
            comment,
            isDecisionMaker
        )
    }
}