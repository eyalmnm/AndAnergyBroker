package com.usenergysolutions.energybroker.view.maps

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import kotlinx.android.synthetic.main.activity_add_new_note.*

class AddNewNoteActivity : AppCompatActivity() {
    private val TAG: String = "AddNewNoteActivity"

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_add_new_note)
        setFinishOnTouchOutside(false)

        context = this

//        var placeId = intent.getStringExtra(Constants.USE_PLACE_ID)
//        addNoteTitle.text = resources.getString(R.string.add_note_title_text, placeId)
//        addNoteTitle.text = resources.getString(R.string.add_note_title_text_old, placeId)

        // Handle Send button event
        addNoteEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    saveButton.performClick()
                    return true
                }
                return false
            }
        })

        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        saveButton.setOnClickListener {
            val intent: Intent = Intent()
            intent.putExtra(Constants.APP_DATA, addNoteEditText.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}