package com.usenergysolutions.energybroker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.model.DayOpeningModel
import kotlinx.android.synthetic.main.view_day_opening.view.*

//class DayOpeningView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
//    RelativeLayout(context, attrs) {

class DayOpeningView(val dayOpeningModel: DayOpeningModel, context: Context, attrs: AttributeSet? = null) :
    RelativeLayout(context, attrs) {
    private val TAG: String = "DayOpeningView"

    private lateinit var layout: RelativeLayout

    init {
        layout = View.inflate(context, R.layout.view_day_opening, null) as RelativeLayout
        initUi()
    }

    private fun initUi() {
        dayName.text = dayOpeningModel.name ?: ""

        if (dayOpeningModel.isClosed!!) {
            dayOpeningTimeContainer.visibility = View.GONE
            dayClosed.visibility = View.VISIBLE
        } else {
            dayOpeningTimeContainer.visibility = View.VISIBLE
            dayClosed.visibility = View.GONE

            // Set Hours
            dayFrmHrs.text = dayOpeningModel.frmHrs ?: "00"
            dayFrmMin.text = dayOpeningModel.frmMin ?: "00"
            dayFrmType.text = dayOpeningModel.frmTypeStr ?: "AM"

            dayTilHrs.text = dayOpeningModel.tilHrs ?: "00"
            dayTilMin.text = dayOpeningModel.tilMin ?: "00"
            dayTilType.text = dayOpeningModel.tilTypeStr ?: "PM"
        }
    }
}