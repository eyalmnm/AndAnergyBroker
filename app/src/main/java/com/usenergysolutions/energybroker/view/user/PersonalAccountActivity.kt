package com.usenergysolutions.energybroker.view.user

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.model.DataWrapper
import com.usenergysolutions.energybroker.model.PersonalNoteModel
import com.usenergysolutions.energybroker.model.UserModel
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.view.user.adapters.PersonalNotesAdapter
import com.usenergysolutions.energybroker.viewmodel.UserViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

// TODO Continue using
// https://developer.android.com/guide/topics/ui/layout/recyclerview

class PersonalAccountActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val TAG: String = "PersonalAccountActivity"

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

    // UI Components
    private lateinit var avatarImageView: ImageView
    private lateinit var avatarEditButton: ImageView
    private lateinit var fullNameTextView: TextView
    private lateinit var positionTextView: TextView

    private lateinit var personalSalary: TextView
    private lateinit var personalCommission: TextView
    private lateinit var personalBonuses: TextView

    private lateinit var personalEmptyNotesListImage: ImageView
    private lateinit var personalNotesList: ListView

    // ListView Helpers
    private var notesArray: MutableList<PersonalNoteModel> = arrayListOf()
    private lateinit var notesAdapter: PersonalNotesAdapter

    // Communication
    private var viewModel: UserViewModel? = null
    private var progressDialog: ProgressDialog? = null

    // Helpers
    private lateinit var context: Context

    // Data Holder
    private var userModel: UserModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_account)
        context = this
        Log.d(TAG, "onCreate")

        // Fabric.with(this, Crashlytics())
        Crashlytics.log("$TAG onCreate")


        // Bind header UI Components
        avatarImageView = findViewById(R.id.avatarImageView)
        avatarEditButton = findViewById(R.id.avatarEditButton)
        fullNameTextView = findViewById(R.id.fullNameTextView)
        positionTextView = findViewById(R.id.positionTextView)

        // Bind header UI Components
//        personalSalary = findViewById(R.id.personalSalary)
//        personalCommission = findViewById(R.id.personalCommission)
//        personalBonuses = findViewById(R.id.personalBonuses)

        // Bind list components
        personalEmptyNotesListImage = findViewById(R.id.personalEmptyNotesListImage)
        personalNotesList = findViewById(R.id.personalNotesList)

        // Bind the Map Model View
        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        // Init ListView Components
        notesAdapter = PersonalNotesAdapter(context, notesArray)
        personalNotesList.adapter = notesAdapter
        personalNotesList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Log.d(TAG, "onItemClick")
                Toast.makeText(context, "itme $position clicked", Toast.LENGTH_SHORT).show()
            }
        switchView()
        loadData()

        // Init Avatar Image Edit Button
        avatarEditButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openEditMyAvaterScreen()
            }
        })
        avatarEditButton.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        getPermissions()
    }


    private fun loadData() {
        showLoadingDialog()
        viewModel?.getMyProfileData()?.observe(this@PersonalAccountActivity,
            Observer<DataWrapper<UserModel>> { t ->
                progressDialog?.dismiss()
                notesArray.clear()
                if (t?.data != null) {
                    userModel = t.data
                    if (t.data?.avatar != null) {
                        Glide.with(context).load(t.data?.avatar).apply(RequestOptions.circleCropTransform())
                            .into(avatarImageView)
                    }
                    fullNameTextView.text = t.data?.name
//                    positionTextView.text = t.data?.status        // TODO
//                    if (t.data?.getNotes() != null) {             // TODO
//                        notesArray.addAll(t.data?.getNotes()!!)   // TODO
//                    }                                             // TODO
                } else {
                    Toast.makeText(
                        context,
                        "Failed to retrieve profile's data " + t?.throwable?.message,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                notesAdapter.notifyDataSetChanged()
                switchView()
            })
    }

    private fun switchView() {
        if (notesArray.isEmpty()) {
            personalEmptyNotesListImage.visibility = View.VISIBLE
            personalNotesList.visibility = View.GONE
        } else {
            personalEmptyNotesListImage.visibility = View.GONE
            personalNotesList.visibility = View.VISIBLE
        }
    }

    private fun allowSettingAvatar() {
        // TODO not implemented
        avatarEditButton.isEnabled = true
    }

    private fun openEditMyAvaterScreen() {
        // TODO not implemented
        val intent: Intent = Intent(context, ImagePickerDialog::class.java)
        if (userModel != null && userModel?.avatar != null) {
            intent.data = Uri.parse(userModel?.avatar)
        }
        startActivityForResult(intent, RC_IMAGE_PICKER)
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    private fun loadNotes() {
        // TODO not implemented
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

    // Add animation to back press
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
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