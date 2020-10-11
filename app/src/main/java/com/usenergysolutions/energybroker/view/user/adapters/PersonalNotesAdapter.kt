package com.usenergysolutions.energybroker.view.user.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.model.PersonalNoteModel

class PersonalNotesAdapter(var context: Context, var notesArray: MutableList<PersonalNoteModel>) : BaseAdapter() {
    private val TAG: String = "PersonalNotesAdapter"

    private lateinit var inflater: LayoutInflater

    init {
        Log.d(TAG, "init")
        inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.item_personal_note_lists, null)
        }

        var viewHolder: ViewHolder? = view?.tag as ViewHolder?
        if (viewHolder == null) {
            viewHolder = ViewHolder()
            viewHolder.noteTextView = view!!.findViewById(R.id.textView)
            view.tag = viewHolder
        }

        // Set Data
        viewHolder.noteTextView?.text = notesArray[position].getNote()

        return view!!
    }

    override fun getItem(position: Int): Any {
        return notesArray[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return notesArray.size
    }


    class ViewHolder {
        var noteTextView: TextView? = null
    }
}