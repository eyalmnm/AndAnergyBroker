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
import com.usenergysolutions.energybroker.model.MeetingModel

class MeetingsAdapter(var context: Context, var meetingsArray: MutableList<MeetingModel>) : BaseAdapter() {
    private val TAG: String = "MeetingsAdapter"

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

        val meetingModel: MeetingModel = meetingsArray[position]
        if (meetingModel.getAvatar() != null && meetingModel.getAvatar()?.isNotEmpty()!!) {
            Glide.with(context).load(meetingModel.getAvatar()).apply(RequestOptions.circleCropTransform())
                .into(viewHolder.avatarImageView!!)
        }

        viewHolder.nameTextView?.text = meetingModel.getUserName()
        val statusName: String? = meetingModel.getFormattedStatus() ?: ""
        val reminderStr: String? = meetingModel.getReminder() ?: ""
        viewHolder.meetingStatus?.text = context.resources.getString(
            R.string.meeting_status_string, statusName, reminderStr
        )
        viewHolder.noteTextView?.text = meetingModel.getNotes()
        viewHolder.dateTextView?.text = meetingModel.getCreatedAt12H()

        return view!!
    }

    override fun getItem(position: Int) = meetingsArray.get(position)

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = meetingsArray.size


    class ViewHolder {
        var avatarImageView: ImageView? = null
        var nameTextView: TextView? = null
        var meetingStatus: TextView? = null
        var noteTextView: TextView? = null
        var dateTextView: TextView? = null
    }
}