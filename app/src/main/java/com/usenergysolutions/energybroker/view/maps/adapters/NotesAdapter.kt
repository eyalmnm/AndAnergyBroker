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
import com.usenergysolutions.energybroker.model.NoteModel

class NotesAdapter(var context: Context, var notesArray: MutableList<NoteModel>) : BaseAdapter() {
    private val TAG: String = "NotesAdapter"

    private lateinit var inflater: LayoutInflater

    init {
        Log.d(TAG, "init")
        inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.item_add_note_lists, null)
        }

        var viewHolder: ViewHolder? = view?.tag as ViewHolder?
        if (viewHolder == null) {
            viewHolder = ViewHolder()
            viewHolder.avatarImageView = view?.findViewById(R.id.addNoteAvatarImageView)
            viewHolder.nameTextView = view?.findViewById(R.id.addNoteNameTextView)
            viewHolder.noteTextView = view?.findViewById(R.id.addNoteTextView)
            viewHolder.dateTextView = view?.findViewById(R.id.addNoteDateTextView)
            view!!.tag = viewHolder
        }

        val noteModel: NoteModel = notesArray.get(position)
        if (noteModel.getAvatarUrl() != null && noteModel.getAvatarUrl()?.isNotEmpty()!!) {
            Glide.with(context).load(noteModel.getAvatarUrl()).apply(RequestOptions.circleCropTransform())
                .into(viewHolder.avatarImageView!!)
        }

        viewHolder.nameTextView?.text = noteModel.getCreatorName()
        viewHolder.noteTextView?.text = noteModel.getNoteText()
        viewHolder.dateTextView?.text = noteModel.getDateTime12H()

        return view!!
    }

    override fun getItem(position: Int) = notesArray[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = notesArray.size


    class ViewHolder {
        var avatarImageView: ImageView? = null
        var nameTextView: TextView? = null
        var noteTextView: TextView? = null
        var dateTextView: TextView? = null
    }
}