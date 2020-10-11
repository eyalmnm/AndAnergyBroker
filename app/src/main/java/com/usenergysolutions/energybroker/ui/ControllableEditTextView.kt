package com.usenergysolutions.energybroker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.usenergysolutions.energybroker.R

class ControllableEditTextView(
    context: Context,
    val inputType: Int,
    var listener: OnClickListener?,
    var index: Int,
    var hint: String,
    attrs: AttributeSet? = null
) :
    LinearLayout(context, attrs) {
    private val TAG: String = "ControllableEditTextView"

    interface OnClickListener {
        fun onClick(parentView: View, index: Int, inputType: Int)
    }

    // UI Components
    private var layout: View = View.inflate(context, R.layout.view_controllable_edit_text, null)
    private var controllableEditText: EditText? = null
    private var controllableTextView: TextView? = null
    private var controllableIcon: ImageView? = null

    private val layoutParams: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    init {
        setBackgroundColor(resources.getColor(android.R.color.transparent, null))

        controllableEditText = layout.findViewById(R.id.controllableEditText)
        controllableTextView = layout.findViewById(R.id.controllableTextView)
        controllableIcon = layout.findViewById(R.id.controllableIcon)

        controllableEditText?.inputType = inputType
        controllableEditText?.hint = hint
        controllableTextView?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                controllableIcon?.performClick()
            }
        })

        controllableIcon?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (listener != null) {
                    listener?.onClick(this@ControllableEditTextView, index, inputType)
                }
            }
        })

        addView(layout, layoutParams)
    }

    fun changeIcon(addIcon: Boolean, index: Int) {
        this.index = index
        if (addIcon) {
            controllableTextView?.text = context.resources.getText(R.string.add_new_form)
            controllableIcon?.setImageResource(R.drawable.ic_add_white)
        } else {
            controllableTextView?.text = context.resources.getText(R.string.remove_form)
            controllableIcon?.setImageResource(R.drawable.ic_remove_circle_outline_white_24dp)
        }
    }


    fun getText(): String? {
        return controllableEditText?.text.toString()
    }

    fun setText(text: String) {
        controllableEditText?.setText(text)
    }
}