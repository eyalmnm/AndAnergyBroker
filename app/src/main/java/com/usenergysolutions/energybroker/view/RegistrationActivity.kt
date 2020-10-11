package com.usenergysolutions.energybroker.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.viewmodel.UserViewModel

class RegistrationActivity : AppCompatActivity() {
    private val TAG: String = "RegistrationActivity"

    // UI Components
    private lateinit var registrationNameEditText: EditText
    private lateinit var registrationUserNameEditText: EditText
    private lateinit var registrationEmailEditText: EditText
    private lateinit var registrationPasswordEditText: EditText
    private lateinit var registrationConfirmPasswordEditText: EditText
    private lateinit var registrationEulaCheckBox: CheckBox
    private lateinit var registrationCreateAccountButton: Button
    private lateinit var alreadyMemberTextView: TextView

    // Helpers
    private lateinit var context: Context
    private var agreeEula: Boolean = false

    // Connection
    private lateinit var viewModel: UserViewModel
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registertion)
        Log.d(TAG, "onCreate")
        context = this

        // Bind View Model
        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        // Bind UI Component
        registrationNameEditText = findViewById(R.id.registrationNameEditText)
        registrationUserNameEditText = findViewById(R.id.registrationUserNameEditText)
        registrationEmailEditText = findViewById(R.id.registrationEmailEditText)
        registrationPasswordEditText = findViewById(R.id.registrationPasswordEditText)
        registrationConfirmPasswordEditText = findViewById(R.id.registrationConfirmPasswordEditText)
        registrationEulaCheckBox = findViewById(R.id.registrationEulaCheckBox)
        registrationCreateAccountButton = findViewById(R.id.registrationCreateAccountButton)
        alreadyMemberTextView = findViewById(R.id.alreadyMemberTextView)

        // Detect clicks
        registrationConfirmPasswordEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    registrationCreateAccountButton.performClick()
                    return true
                }
                return false
            }
        })
        registrationEulaCheckBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                agreeEula = isChecked
            }

        })
//        registrationCreateAccountButton.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                val name: String = registrationNameEditText.text.toString()
//                val username: String = registrationUserNameEditText.text.toString()
//                val email: String = registrationEmailEditText.text.toString()
//                val pass: String = registrationPasswordEditText.text.toString()
//                val passCon: String = registrationConfirmPasswordEditText.text.toString()
//                if (pass != passCon && pass.isNotEmpty()) {
//                    Toast.makeText(context, R.string.registration_password_not_equals_msg, Toast.LENGTH_LONG).show()
//                    return
//                }
//                if (name.isEmpty()) {
//                    Toast.makeText(context, R.string.registration_name_empty_msg, Toast.LENGTH_LONG).show()
//                    return
//                }
//                if (username.isEmpty()) {
//                    Toast.makeText(context, R.string.registration_user_empty_msg, Toast.LENGTH_LONG).show()
//                    return
//                }
//                if (email.isEmpty()) {
//                    Toast.makeText(context, R.string.registration_email_empty_msg, Toast.LENGTH_LONG).show()
//                    return
//                }
//                if (pass.isEmpty()) {
//                    Toast.makeText(context, R.string.registration_password_empty_msg, Toast.LENGTH_LONG).show()
//                    return
//                }
//                if (passCon.isEmpty()) {
//                    Toast.makeText(context, R.string.registration_con_pass_empty_msg, Toast.LENGTH_LONG).show()
//                    return
//                }
//                if (progressDialog == null) {
//                    progressDialog = ProgressDialog.newInstance("Loading...")
//                }
//                showDialog(progressDialog!!)
//                viewModel.register(name, username, email, pass)
//                    .observe(this@RegistrationActivity, object : Observer<DataWrapper<String>> {
//                        override fun onChanged(t: DataWrapper<String>?) {
//                            progressDialog!!.dismiss()
//                            if (t!!.throwable == null) {
//                                Dynamic.uuid = t.data
//                                val intent = Intent(context, MapActivity::class.java)
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                                startActivity(intent)
//                                overridePendingTransition(R.anim.activity_slide_out, R.anim.activity_slide_in)
//                                finish()
//                            } else {
//                                Toast.makeText(
//                                    context,
//                                    "Registration failed ${t.throwable!!.message}",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                        }
//
//                    })
//            }
//
//        })
        alreadyMemberTextView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
                finish()
            }
        })
    }

    private fun showDialog(dialog: DialogFragment?) {
        dialog!!.show(supportFragmentManager, dialog::class.java.simpleName)
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    override fun onBackPressed() {
        alreadyMemberTextView.performClick()
    }
}