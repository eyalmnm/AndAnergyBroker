package com.usenergysolutions.energybroker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.usenergysolutions.energybroker.R

class AddedMeetingContactView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {
    private val TAG: String = "AddedMeetingContactView"

    // UI Components
    private var layout: View = View.inflate(context, R.layout.view_added_meeting_contact, null)
    private var contactNameTextView: TextView? = null
    private var contactPhoneTextView: TextView? = null
    private var contactMailTextView: TextView? = null
    private var contactCommentTextView: TextView? = null

    private val layoutParams: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    init {
        setBackgroundColor(resources.getColor(android.R.color.transparent, null))

        // Bind UI Components
        contactNameTextView = layout.findViewById(R.id.contactNameTextView)
        contactPhoneTextView = layout.findViewById(R.id.contactPhoneTextView)
        contactMailTextView = layout.findViewById(R.id.contactMailTextView)
        contactCommentTextView = layout.findViewById(R.id.contactCommentTextView)

        addView(layout, layoutParams)
    }

    fun setData(name: String?, phone: String?, email: String?, comment: String?) {
        contactNameTextView?.text = name ?: ""
        contactPhoneTextView?.text = phone ?: ""
        contactMailTextView?.text = email ?: ""
        contactCommentTextView?.text = comment ?: ""
    }
}