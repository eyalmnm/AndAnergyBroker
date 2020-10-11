package com.usenergysolutions.energybroker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.model.UesPlaceModel
import com.usenergysolutions.energybroker.utils.CalendarUtils

class AddedPlaceView(var listener: OnPlaceViewClickListener?, context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs), View.OnClickListener {
    private val TAG: String = "AddedPlaceView"

    interface OnPlaceViewClickListener {
        fun onPlaceViewClick(placeModel: UesPlaceModel?)
    }

    private var placeModel: UesPlaceModel? = null

    // UI Components
    private var layout: View = View.inflate(context, R.layout.view_added_place, null)
    private var businessNameTv: TextView? = null
    private var businessAddressTv: TextView? = null
    private var creationTimeTv: TextView? = null

    private val layoutParams: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    init {
        setBackgroundColor(resources.getColor(android.R.color.transparent, null))

        // Bind UI Components
        businessNameTv = layout.findViewById(R.id.businessName)
        businessAddressTv = layout.findViewById(R.id.businessAddress)
        creationTimeTv = layout.findViewById(R.id.creationTime)

        addView(layout, layoutParams)

        layout.setOnClickListener(this)
    }

    fun setPlaceData(placeModel: UesPlaceModel?) {
        this.placeModel = placeModel
        businessNameTv?.text = placeModel?.businessName ?: ""
        businessAddressTv?.text = placeModel?.businessAddress ?: ""
        var lastDate: String? = placeModel?.creationDate
        if (placeModel?.updateDate != null && placeModel.updateDate?.contains("0000-00", true) == false) {
            lastDate = placeModel.updateDate
        }
        if (lastDate != null) {
            creationTimeTv?.text = CalendarUtils.convertTo12H(lastDate)
        } else {
            creationTimeTv?.text = null
        }
    }

    override fun onClick(v: View?) {
        if (listener != null) {
            listener?.onPlaceViewClick(placeModel)
        }
    }
}