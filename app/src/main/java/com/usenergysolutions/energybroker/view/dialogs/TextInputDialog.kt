package com.usenergysolutions.energybroker.view.dialogs

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants

class TextInputDialog : DialogFragment() {
    private val TAG: String = "TextInputDialog"

    // Communication interface
    interface OnTextSavedListener {
        fun onTextSaved(text: String)
    }

    // Static Creator
    companion object {

        fun newInstance(listener: OnTextSavedListener): TextInputDialog {
            val ret: TextInputDialog = TextInputDialog()
            ret.listener = listener
            return ret
        }
    }

    var listener: OnTextSavedListener? = null

    // UI Components
    private lateinit var editText: EditText
    private lateinit var cancelButton: TextView
    private lateinit var okButton: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        isCancelable = true
        return inflater.inflate(R.layout.dialog_text_input, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editText = view.findViewById(R.id.editText)
        cancelButton = view.findViewById(R.id.cancelButton)
        okButton = view.findViewById(R.id.okButton)

        var arguments: Bundle? = arguments
        if (arguments != null) {
            var text: String = arguments.getString(Constants.NAME_TEXT_DATA, "")
            editText.setText(text)
        }

        cancelButton.setOnClickListener {
            hideKeyboard()
            dismiss()
        }

        okButton.setOnClickListener {
            if (listener != null) {
                listener?.onTextSaved(editText.text.toString())
            }
            hideKeyboard()
            dismiss()
        }
    }

    // Hide VirtualKeyboard
    private fun hideKeyboard() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}