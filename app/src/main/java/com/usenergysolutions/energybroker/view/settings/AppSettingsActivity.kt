package com.usenergysolutions.energybroker.view.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Dynamic
import com.usenergysolutions.energybroker.utils.AppUtils
import com.usenergysolutions.energybroker.utils.PreferencesUtils
import com.usenergysolutions.energybroker.utils.StringUtils
import com.usenergysolutions.energybroker.view.MainActivity
import com.usenergysolutions.energybroker.view.dialogs.AppLogoutDialog
import kotlinx.android.synthetic.main.activity_app_setting.*

class AppSettingsActivity : AppCompatActivity(), AppLogoutDialog.OnLogoutPressListener {
    private val TAG: String = "AppSettingsActivity"

    // UI Components
    private lateinit var settingsRememberMeCheckBox: CheckBox
    private lateinit var versionNumber: TextView

    // Helpers
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_setting)
        Log.d(TAG, "onCreate")
        context = this

        // Bind UI Components
        settingsRememberMeCheckBox = findViewById(R.id.settingsRememberMeCheckBox)
        versionNumber = findViewById(R.id.versionNumber)


        // Init UI Components
        settingsRememberMeCheckBox.isChecked = PreferencesUtils.getInstance(context).uuid != null


        // Add Listeners
        settingsRememberMeCheckBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked && !StringUtils.isNullOrEmpty(Dynamic.uuid)) {
                    PreferencesUtils.getInstance(context).uuid = Dynamic.uuid
                } else {
                    PreferencesUtils.getInstance(context).removeUUID()
                }
            }
        })

        versionNumber.text = getString(
            R.string.app_version_format,
            AppUtils.getAppVersion(this), AppUtils.getAppVersionCode(this)
        )

        settingLogOutButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                showExitDialog()
            }
        })
    }

    private fun showExitDialog() {
        val appLogoutDialog = AppLogoutDialog()
        appLogoutDialog.show(supportFragmentManager, "AppLogoutDialog")
    }

    // AppLogoutDialog.OnLogoutPressListener implementation
    override fun onLogoutPressed(logout: Boolean) {
        if (logout) {
            settingsRememberMeCheckBox.isChecked = false
            PreferencesUtils.getInstance(context).removeUUID()
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
        }
    }

    // Support the back animation
    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
    }

    override fun onBackPressed() {
        finish()
    }
}