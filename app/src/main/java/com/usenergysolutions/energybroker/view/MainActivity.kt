package com.usenergysolutions.energybroker.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.BuildConfig
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.config.Dynamic
import com.usenergysolutions.energybroker.model.DataWrapper
import com.usenergysolutions.energybroker.model.UserModel
import com.usenergysolutions.energybroker.model.incoming.user.LoginResponse
import com.usenergysolutions.energybroker.utils.GradleUtil
import com.usenergysolutions.energybroker.utils.PreferencesUtils
import com.usenergysolutions.energybroker.utils.ScreenSizeUtils
import com.usenergysolutions.energybroker.utils.StringUtils
import com.usenergysolutions.energybroker.view.dialogs.AppExitDialog
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.view.maps.MapActivity
import com.usenergysolutions.energybroker.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {
    private val TAG: String = "MainActivity"

    // UI Components
    lateinit var loginUserName: EditText
    lateinit var loginPassword: EditText

    lateinit var loginSignInButton: Button
    lateinit var loginSignUp: TextView

    lateinit var loginRememberMeCheckBox: CheckBox

    lateinit var versionNumber: TextView

    // Helpers
    private lateinit var context: Context
    private var rememberMe: Boolean = false

    // Connection
    private lateinit var viewModel: UserViewModel
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")

        Crashlytics.log("$TAG onCreate")

        context = this

        // Bind View Model
        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        // Bind UI Component
        loginUserName = findViewById(R.id.loginUserName)
        loginPassword = findViewById(R.id.loginPassword)
        loginSignInButton = findViewById(R.id.loginSignInButton)
        loginSignUp = findViewById(R.id.loginSignUp)
        loginRememberMeCheckBox = findViewById(R.id.loginRememberMeCheckBox)
        versionNumber = findViewById(R.id.versionNumber)

        // Init Version name and number
        val versionName: String = GradleUtil.getAppVersion(this) ?: ""
        val versionCode: Int = GradleUtil.getAppVersionCode(this) ?: 0
        versionNumber.text = getString(R.string.app_version_format, versionName, versionCode)

        loginPassword.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    loginSignInButton.performClick()
                    return true
                }
                return false
            }
        })

        loginSignInButton.setOnClickListener {
            val usr = loginUserName.text.toString()
            val pwd = loginPassword.text.toString()
            if (usr.isNotEmpty() && pwd.isNotEmpty()) {
                if (progressDialog == null) {
                    progressDialog = ProgressDialog.newInstance("Login...")
                }
                showDialog(progressDialog!!)
                viewModel.login(usr, pwd).observe(this@MainActivity, object : Observer<DataWrapper<LoginResponse>> {
                    override fun onChanged(t: DataWrapper<LoginResponse>?) {
                        progressDialog!!.dismiss()
                        if (t?.data != null) {
                            val user: UserModel? = t.data!!.user
                            Dynamic.uuid = user?.uuid
                            if (Dynamic.uuid == null) {
                                Toast.makeText(context, "Login failed. Please try again!", Toast.LENGTH_LONG).show()
                                return
                            }
                            PreferencesUtils.getInstance(context).offLineUuid =
                                Dynamic.uuid // Save the UUID for logged out use.
                            if (rememberMe) {
                                PreferencesUtils.getInstance(context).uuid = Dynamic.uuid
                            }
                            moveToNextScreen(user!!)
                        } else {
                            Toast.makeText(context, "Login failed ${t?.throwable?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                })
            } else {
                var msg: Int = R.string.login_missing_credentials
                if (usr.isNotEmpty() && usr.contains(resources.getString(R.string.developer_phone))) {
                    msg = R.string.developer_name
                }
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }
        }

        loginSignUp.setOnClickListener {
            val intent = Intent(context, RegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            finish()
        }

        loginRememberMeCheckBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                rememberMe = isChecked
                if (rememberMe && !StringUtils.isNullOrEmpty(Dynamic.uuid)) {
                    PreferencesUtils.getInstance(context).uuid = Dynamic.uuid
                } else {
                    PreferencesUtils.getInstance(context).removeUUID()
                }
            }

        })

        if (PreferencesUtils.getInstance(context).uuid != null) {
            Dynamic.uuid = PreferencesUtils.getInstance(context).uuid
            rememberMe = true
            loginRememberMeCheckBox.isChecked = true
            Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    moveToNextScreen(null)
                }
            }, 50)
        } else {
            rememberMe = false
            loginRememberMeCheckBox.isChecked = false
        }

        if (BuildConfig.DEBUG) {
            ScreenSizeUtils.differentDensityAndScreenSize(context)
        }
    }

    private fun moveToNextScreen(user: UserModel?) {
        val intent = Intent(context, MapActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (user != null) {
            intent.putExtra(Constants.APP_DATA, user)  // TODO Use this data.
        }
        startActivity(intent)
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
        finish()
    }

    private fun showDialog(dialog: DialogFragment?) {
        dialog!!.show(supportFragmentManager, dialog::class.java.simpleName)
    }

    private fun showExitDialog() {
        val appExitDialog = AppExitDialog()
        appExitDialog.show(supportFragmentManager, "AppExitDialog")
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    override fun onBackPressed() {
        showExitDialog()
    }
}
