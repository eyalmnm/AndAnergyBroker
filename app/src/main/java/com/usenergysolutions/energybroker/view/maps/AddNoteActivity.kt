package com.usenergysolutions.energybroker.view.maps

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.model.DataWrapper
import com.usenergysolutions.energybroker.model.NoteModel
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.view.maps.adapters.NotesAdapter
import com.usenergysolutions.energybroker.viewmodel.MapViewModel

class AddNoteActivity : AppCompatActivity() {
    private val TAG: String = "AddNoteActivity"

    private var placeId: String? = null

    // UI Components
    private lateinit var addNoteEmptyNotesListImage: ImageView
    private lateinit var addNoteNotesListView: ListView

    private lateinit var noteEditText: EditText
    private lateinit var sendButton: ImageButton

    // ListView Helpers
    private var notesArray: MutableList<NoteModel> = arrayListOf()
    private lateinit var notesAdapter: NotesAdapter

    // Communication
    private var viewModel: MapViewModel? = null

    // Helper
    private lateinit var context: Context

    // Loading Dialog
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        Log.d(TAG, "onCreate")
        context = this

        Crashlytics.log("$TAG onCreate")

        placeId = intent.extras.getString(Constants.USE_PLACE_ID, null)
        if (placeId == null) onBackPressed()

        // Bind UI Component
        noteEditText = findViewById(R.id.noteEditText)
        sendButton = findViewById(R.id.sendButton)

        addNoteEmptyNotesListImage = findViewById(R.id.addNoteEmptyNotesListImage)
        addNoteNotesListView = findViewById(R.id.addNoteNotesListView)

        // Bind the Map Model View
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        // Init ListView Components
        notesAdapter = NotesAdapter(context, notesArray)
        addNoteNotesListView.adapter = notesAdapter
        addNoteNotesListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Log.d(TAG, "onItemClick")
                Toast.makeText(context, "itme $position clicked", Toast.LENGTH_SHORT).show()
            }
        switchView()
        loadData()

        // Init note EditText
        noteEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                sendButton.isEnabled = s?.length!! > 0
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Init note EditText
        noteEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendButton.performClick()
                    return true
                }
                return false
            }
        })

        // Init Send Note Button
        sendButton.setOnClickListener { sendNote() }
    }

    private fun loadData() {
        showLoadingDialog()
        viewModel?.getNotes(placeId!!)?.observe(this@AddNoteActivity,
            Observer<DataWrapper<List<NoteModel>>> { t ->
                progressDialog?.dismiss()
                notesArray.clear()
                if (t?.data != null) {
                    notesArray.addAll(t.data as MutableList<NoteModel>)
                    if (!notesArray.isNullOrEmpty()) {
                        notesArray.reverse()
                    }
                } else {
                    if (t?.throwable?.message!!.isNotEmpty()) {
                        Toast.makeText(context, "Failed to retrieve notes " + t.throwable?.message, Toast.LENGTH_LONG)
                            .show()
                    } else {
                        notesAdapter.notifyDataSetChanged()
                        Toast.makeText(context, "Notes not found for this place", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                notesAdapter.notifyDataSetChanged()
                switchView()
            })
    }

    private fun sendNote() {
        showSendingDialog()
        val orgNote: String = noteEditText.text.toString()
        viewModel?.addNote(placeId!!, orgNote)
            ?.observe(this@AddNoteActivity, object : Observer<DataWrapper<NoteModel>> {
                override fun onChanged(t: DataWrapper<NoteModel>?) {
                    progressDialog?.dismiss()
                    if (t?.data != null) {
                        val theNote: String = t.data?.getNoteText()!!
                        if (orgNote == theNote) {
                            loadData()
                        }
                        Toast.makeText(context, "Note: $theNote - added successful", Toast.LENGTH_SHORT).show()
                        noteEditText.text = null
                    } else {
                        Toast.makeText(context, "Failed to send note " + t?.throwable?.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
    }

    private fun showSendingDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog()
        }
        val arg: Bundle = Bundle()
        arg.putString(Constants.APP_DATA, "Sending...")
        progressDialog!!.arguments = arg
        progressDialog!!.show(supportFragmentManager, progressDialog!!::class.java.simpleName)
    }

    private fun showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog()
        }
        progressDialog!!.show(supportFragmentManager, progressDialog!!::class.java.simpleName)
    }

    private fun switchView() {
        if (notesArray.isEmpty()) {
            addNoteEmptyNotesListImage.visibility = View.VISIBLE
            addNoteNotesListView.visibility = View.GONE
        } else {
            addNoteEmptyNotesListImage.visibility = View.GONE
            addNoteNotesListView.visibility = View.VISIBLE
        }
    }

    // Add animation to back press
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
    }
}