package com.usenergysolutions.energybroker.view.adapters

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.model.PlaceTypeModel


class PlacesTypeAdapter(
    private val activity: AppCompatActivity,
    private val itextViewResourceId: Int,
    private val placesTypes: ArrayList<PlaceTypeModel>
) :
    ArrayAdapter<PlaceTypeModel>(activity, itextViewResourceId, placesTypes) {

    private var inflater: LayoutInflater? = null

    init {
        inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView
        if (row == null) {
            row = inflater?.inflate(itextViewResourceId, parent, false)
        }
        val textView: TextView = row?.findViewById(com.usenergysolutions.energybroker.R.id.datTextView)!!
        textView.gravity = Gravity.START
        textView.setBackgroundColor(context.resources.getColor(android.R.color.transparent))
        textView.text = placesTypes.get(position).name
        return row
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = View.inflate(context, com.usenergysolutions.energybroker.R.layout.add_place_dropdown, null)
        val textView = view.findViewById(com.usenergysolutions.energybroker.R.id.dropdown) as TextView
        textView.text = placesTypes.get(position).name

        textView.setBackgroundColor(context.resources.getColor(R.color.add_place_dropdown_bg_color))

        return view
    }
}