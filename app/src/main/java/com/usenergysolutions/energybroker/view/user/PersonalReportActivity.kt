package com.usenergysolutions.energybroker.view.user

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.android.datetimepicker.date.DatePickerDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.model.*
import com.usenergysolutions.energybroker.ui.AddedMeetingView
import com.usenergysolutions.energybroker.ui.AddedPlaceView
import com.usenergysolutions.energybroker.utils.CalendarUtils
import com.usenergysolutions.energybroker.utils.StringUtils
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.view.maps.BusinessEditActivity
import com.usenergysolutions.energybroker.view.maps.MeetingEditActivity
import com.usenergysolutions.energybroker.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_personal_report.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class PersonalReportActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    AddedMeetingView.OnMeetingViewClickListener, AddedPlaceView.OnPlaceViewClickListener {
    private val TAG: String = "PersonalReportActivity"

    // Permissions components
    companion object {
        const val RC_APP_PERMISSION = 123
        const val RC_IMAGE_PICKER = 124
    }

    private val appPermissions: Array<String> = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var permissionGranted: Boolean = false

    // Communication
    private var viewModel: UserViewModel? = null
    private var progressDialog: ProgressDialog? = null

    // Helpers
    private lateinit var context: Context
    private var now: Long = CalendarUtils.getCurrentLocalTimeLong()

    // Data Holder
    private var userModel: UserModel? = null
    private var dailyReportModel: DailyReportModel? = null

    // UI Helper
    private val layoutParams: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_report)
        context = this
        Log.d(TAG, "onCreate")

        // Fabric.with(this, Crashlytics())
        Crashlytics.log("$TAG onCreate")

        // Bind the Map Model View
        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        // Init Places and meetings layout params
        val margin: Int = resources.getDimensionPixelSize(R.dimen.spacing_1x)
        layoutParams.setMargins(0, margin, 0, margin)

        switchView()
        loadData()

        // Init Avatar Image Edit Button
        avatarEditButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openEditMyAvatarScreen()
            }
        })
        avatarEditButton.isEnabled = false

        datePickerImageView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                showDatePicker()
            }
        })
    }

    private fun showDatePicker() {
        val tempCal = Calendar.getInstance()
        tempCal.timeInMillis = CalendarUtils.getCurrentLocalTimeLong()
        val year = tempCal.get(Calendar.YEAR)
        val month = tempCal.get(Calendar.MONTH)
        val day = tempCal.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog: DatePickerDialog = DatePickerDialog.newInstance(OnDateSet(), year, month, day)
        (datePickerDialog as DialogFragment).show(supportFragmentManager, "datePicker")
    }

    override fun onResume() {
        super.onResume()
        //now =  CalendarUtils.getCurrentLocalTimeLong() // CalendarUtils.getCurrentLocalTime()  // (1554545343743) // Testing time
        getPermissions()
        if (dailyReportModel != null) {
            Log.d(TAG, "OnResume dailyReportModel not null")
            loadDailyReport(CalendarUtils.getCurrentLocalDateString(now))  // (1554545343743) // Testing time
        }
    }

    private fun loadData() {
        showLoadingDialog()
        viewModel?.getMyProfileData()?.observe(this@PersonalReportActivity,
            Observer<DataWrapper<UserModel>> { t ->
                progressDialog?.dismiss()
                if (t?.data != null) {
                    userModel = t.data
                    if (t.data?.avatar != null) {
                        Glide.with(context).load(t.data?.avatar).apply(RequestOptions.circleCropTransform())
                            .into(avatarImageView)
                    }
                    fullNameTextView.text = t.data?.name
                    loadDailyReport(CalendarUtils.getCurrentLocalDateString(now))  // (1554545343743) // Testing time
                } else {
                    Toast.makeText(
                        context,
                        "Failed to retrieve profile's data " + t?.throwable?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun loadDailyReport(date: String) {
        showLoadingDialog()
        viewModel?.getDailyReport(date)?.observe(this@PersonalReportActivity,
            Observer<DataWrapper<DailyReportModel>> { t ->
                progressDialog?.dismiss()
                if (t?.data != null) {
                    dailyReportModel = t.data
                    createReport()
                } else {
                    Toast.makeText(
                        context,
                        "Failed to retrieve daily report data " + t?.throwable?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun createReport() {
        personalNotesList.removeAllViews()
        if (dailyReportModel != null) {

            // Draw Places
            if (!dailyReportModel?.places.isNullOrEmpty()) {

                for (placeModel: UesPlaceModel in dailyReportModel?.places!!) {
                    val addedPlace: AddedPlaceView = AddedPlaceView(this, context)
                    addedPlace.setPlaceData(placeModel)
                    personalNotesList.addView(addedPlace, layoutParams)
                }
            }

            // Draw Meetings
            if (!dailyReportModel?.meetings.isNullOrEmpty()) {

                for (meetingModel: MeetingReportModel in dailyReportModel?.meetings!!) {
                    val addedMeetingView: AddedMeetingView = AddedMeetingView(this, context)
                    addedMeetingView.setData(meetingModel)
                    personalNotesList.addView(addedMeetingView, layoutParams)
                }
            }
        }
        switchView()
    }


    private fun switchView() {
        if (dailyReportModel == null || (dailyReportModel?.places.isNullOrEmpty() && dailyReportModel?.meetings.isNullOrEmpty())) {
            personalEmptyNotesListImage.visibility = View.VISIBLE
            personalNotesList.visibility = View.GONE
        } else {
            personalEmptyNotesListImage.visibility = View.GONE
            personalNotesList.visibility = View.VISIBLE
        }
    }

    private fun allowSettingAvatar() {
        avatarEditButton.isEnabled = true
    }

    private fun openEditMyAvatarScreen() {
        val intent: Intent = Intent(context, ImagePickerDialog::class.java)
        if (userModel != null && userModel?.avatar != null) {
            intent.data = Uri.parse(userModel?.avatar)
        }
        startActivityForResult(intent, PersonalAccountActivity.RC_IMAGE_PICKER)
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    private fun showLoadingDialog() {
        if (progressDialog != null) {
            progressDialog?.dismiss()
            val frag: Fragment? = supportFragmentManager.findFragmentByTag(progressDialog!!::class.java.simpleName)
            if (frag != null) {
                supportFragmentManager.beginTransaction().remove(frag).commit()
            }
        }
        if (progressDialog == null) {
            progressDialog = ProgressDialog()
        }
        progressDialog!!.show(supportFragmentManager, progressDialog!!::class.java.simpleName)
    }

    // AddedPlaceView.OnPlaceViewClickListener Implementation
    override fun onPlaceViewClick(placeModel: UesPlaceModel?) {
        val placeId: String = placeModel?.placeId.toString()
        if (!StringUtils.isNullOrEmpty(placeId)) {
            val intent = Intent(context, BusinessEditActivity::class.java)
            intent.putExtra(Constants.PLACE_ID, placeId)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
        }
    }


    // AddedMeetingView.OnMeetingViewClickListener Implementation
    override fun onMeetingViewClick(meetingReportModel: MeetingReportModel?) {
        if (meetingReportModel != null) {
            // TODO like start meeting results sequence with preloaded data (save = override)
            val intent: Intent = Intent(context, MeetingEditActivity::class.java)
            intent.putExtra(Constants.APP_DATA, meetingReportModel)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
        }
    }

    // Add animation to back press
    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        finish()
        overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
    }


    inner class OnDateSet : DatePickerDialog.OnDateSetListener {

        override fun onDateCanceled(dialog: DatePickerDialog?) {
            // Do Nothing
        }

        override fun onDateSet(dialog: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(year, monthOfYear, dayOfMonth)
            if (calendar.timeInMillis > CalendarUtils.getCurrentLocalTimeLong() + 900) {  // Plus 15 minutes to be safe
                Toast.makeText(context, "You can Not Select date in the future", Toast.LENGTH_LONG).show()
            } else {
                now = calendar.timeInMillis
                loadDailyReport(CalendarUtils.getCurrentLocalDateString(now))
            }
        }

    }

    // ************************************   EasyPermissions   ************************************

    @AfterPermissionGranted(RC_APP_PERMISSION)
    private fun getPermissions() {
        if (hasAppPermissions()) {
            Log.d(TAG, "appPermissionsTask")
            allowSettingAvatar()
        } else {
            EasyPermissions.requestPermissions(
                this,
                context.getString(R.string.must_garnt_permission),
                RC_APP_PERMISSION,
                *appPermissions
            )
        }
    }

    private fun hasAppPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, *appPermissions)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult")

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)

        permissionGranted = true
        allowSettingAvatar()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size)

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (!permissionGranted) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                AppSettingsDialog.Builder(this).build().show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult")

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            val yes = getString(R.string.yes)
            val no = getString(R.string.no)

            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(
                this,
                getString(
                    R.string.returned_from_app_settings_to_activity,
                    if (hasAppPermissions()) yes else no
                ),
                Toast.LENGTH_LONG
            )
                .show()

            // Check if it not happen when permissions have been pre granted
        }
        if (requestCode == RC_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            Glide.with(context).load(data?.data).apply(RequestOptions.circleCropTransform()).into(avatarImageView)
        }
    }

}