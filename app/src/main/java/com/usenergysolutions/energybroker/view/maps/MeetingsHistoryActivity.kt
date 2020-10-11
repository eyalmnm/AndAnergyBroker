package com.usenergysolutions.energybroker.view.maps

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.model.DataWrapper
import com.usenergysolutions.energybroker.model.MeetingModel
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.view.maps.adapters.MeetingsAdapter
import com.usenergysolutions.energybroker.viewmodel.MapViewModel

class MeetingsHistoryActivity : AppCompatActivity() {
    private val TAG: String = "MeetingsHistoryActivity"

    private var placeId: String? = null

    // UI Components
    private lateinit var emptyMeetingsListImage: ImageView
    private lateinit var meetingsListView: ListView

    // ListView Helpers
    private var meetingArray: MutableList<MeetingModel> = arrayListOf()
    private lateinit var meetingsAdapter: MeetingsAdapter

    // Communication
    private var viewModel: MapViewModel? = null

    // Helper
    private lateinit var context: Context

    // Loading Dialog
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meetings_history)
        Log.d(TAG, "onCreate")

        context = this

        Crashlytics.log("$TAG onCreate")

        placeId = intent.extras.getString(Constants.USE_PLACE_ID, null)
        if (placeId == null) onBackPressed()

        // Bind UI Component
        emptyMeetingsListImage = findViewById(R.id.emptyMeetingsListImage)
        meetingsListView = findViewById(R.id.meetingsListView)

        // Bind the Map Model View
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        // Init ListView Components
        meetingsAdapter = MeetingsAdapter(context, meetingArray)
        meetingsListView.adapter = meetingsAdapter
        meetingsListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Log.d(TAG, "onItemClick")
                Toast.makeText(context, "itme $position clicked", Toast.LENGTH_SHORT).show()
            }
        switchView()
        loadData()
    }

    private fun loadData() {
        showLoadingDialog()
        viewModel?.getMeetingsByPlaceId(placeId!!)!!.observe(this, object : Observer<DataWrapper<List<MeetingModel>>> {
            override fun onChanged(t: DataWrapper<List<MeetingModel>>?) {
                progressDialog?.dismiss()
                meetingArray.clear()
                if (t?.data != null) {
                    meetingArray.addAll(t.data!! as MutableList<MeetingModel>)
                } else {
                    if (t?.throwable?.message!!.isNotEmpty()) {
                        Toast.makeText(
                            context,
                            "Failed to retrieve meetings " + t.throwable?.message,
                            Toast.LENGTH_LONG
                        )
                            .show()
                    } else {
                        meetingsAdapter.notifyDataSetChanged()
                        Toast.makeText(context, "Meetings not found for this place", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                meetingsAdapter.notifyDataSetChanged()
                switchView()
            }
        })
    }

    private fun showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog()
        }
        progressDialog!!.show(supportFragmentManager, progressDialog!!::class.java.simpleName)
    }

    private fun switchView() {
        if (meetingArray.isEmpty()) {
            emptyMeetingsListImage.visibility = View.VISIBLE
            meetingsListView.visibility = View.GONE
        } else {
            emptyMeetingsListImage.visibility = View.GONE
            meetingsListView.visibility = View.VISIBLE
        }
    }


    // Add animation to back press
    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
    }
}