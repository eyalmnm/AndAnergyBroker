package com.usenergysolutions.energybroker.view.maps.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.model.BusinessHistoryItem
import com.usenergysolutions.energybroker.utils.CalendarUtils

class BusinessHistoryAdapter(var context: Context, var historyArray: MutableList<BusinessHistoryItem>) : BaseAdapter() {
    private val TAG: String = "BusinessHistoryAdapter"

    private lateinit var inflater: LayoutInflater

    init {
        Log.d(TAG, "init")
        inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.item_meetings_history_lists, null)
        }

        var viewHolder: ViewHolder? = view?.tag as? ViewHolder
        if (viewHolder == null) {
            viewHolder = ViewHolder()
            viewHolder.avatarImageView = view?.findViewById(R.id.addNoteAvatarImageView)
            viewHolder.nameTextView = view?.findViewById(R.id.addNoteNameTextView)
            viewHolder.meetingStatus = view?.findViewById(R.id.meetingStatus)
            viewHolder.noteTextView = view?.findViewById(R.id.addNoteTextView)
            viewHolder.dateTextView = view?.findViewById(R.id.addNoteDateTextView)
            view!!.tag = viewHolder
        }

        val histoyItem: BusinessHistoryItem = historyArray[position]
        if (!histoyItem.getAvatarUrl().isNullOrEmpty()) {
            Glide.with(context).load(histoyItem.getAvatarUrl()).apply(RequestOptions.circleCropTransform())
                .into(viewHolder.avatarImageView!!)
        }

        viewHolder.nameTextView?.text = histoyItem.getUserName()
        val statusName: String? = histoyItem.getStatus() ?: ""
        var reminderStr: String? = histoyItem.getRemainder() ?: ""
        if (reminderStr?.contains("0000-00", true) == true) {
            reminderStr = ""
        }
        if (!reminderStr.isNullOrEmpty()) {
            reminderStr = CalendarUtils.convertTo12H(reminderStr)
        }
        viewHolder.meetingStatus?.text = context.resources.getString(
            R.string.meeting_status_string, statusName, reminderStr
        )
        viewHolder.noteTextView?.text = histoyItem.getNote()
        if (histoyItem.getMostUpdatedDate() != null) {
            viewHolder.dateTextView?.text =
                CalendarUtils.getCurrentLocalTimeString12H(histoyItem.getMostUpdatedDate()!!)
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        return historyArray[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return historyArray.size
    }

    class ViewHolder {
        var avatarImageView: ImageView? = null
        var nameTextView: TextView? = null
        var meetingStatus: TextView? = null
        var noteTextView: TextView? = null
        var dateTextView: TextView? = null
    }

}