package com.usenergysolutions.energybroker.view.maps

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.model.DecisionMakerModel
import com.usenergysolutions.energybroker.model.MeetingReportModel
import com.usenergysolutions.energybroker.utils.CalendarUtils
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.view.maps.gragments.MeetingEditContactFragment
import com.usenergysolutions.energybroker.view.maps.gragments.MeetingEditReviewFragment
import com.usenergysolutions.energybroker.view.maps.gragments.MeetingEditSummaryFragment
import com.usenergysolutions.energybroker.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.activity_meeting_edit.*

class MeetingEditActivity : AppCompatActivity(), MeetingEditContactFragment.OnMeetingEditContactSetListener,
    MeetingEditSummaryFragment.OnMeetingEditSummarySetListener,
    MeetingEditReviewFragment.OnMeetingEditConfirmReviewListener {
    private val TAG: String = "MeetingEditActivity"

    companion object {
        private const val MIN_SCALE = 0.65f
        private const val MIN_ALPHA = 0.3f
    }

    // User's initial Data
    private var meetingReportModel: MeetingReportModel? = null

    // UI Helpers
    private var adapter: MyPagerAdapter? = null

    // Data Helper
    private val businessDetailsParams: HashMap<String, Any> = HashMap()
    private lateinit var context: Context

    // Communication
    private var viewModel: MapViewModel? = null
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_meeting_edit)

        Crashlytics.log("$TAG onCreate")

        // Init communication
        viewModel = ViewModelProviders.of(this@MeetingEditActivity).get(MapViewModel::class.java)

        meetingReportModel = intent.getParcelableExtra(Constants.APP_DATA)
        if (meetingReportModel == null) {
            Log.e(TAG, "onCreate", Exception("meetingReportModel is null"))
            Crashlytics.log("$TAG inCreate meetingReportModel is null")
            onBackPressed()
        } else {
            // Add Fragments to View Pager's adapter
            adapter = MyPagerAdapter(supportFragmentManager)
            addPagerFragments()
            viewPager.adapter = adapter
            viewPager.setPageTransformer(true, this::zoomOutTransformation)
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {}
                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

                override fun onPageSelected(p0: Int) {
                    this@MeetingEditActivity.onPageSelected(p0)
                }
            })
        }

        businessDetailsParams.put(Constants.MEETING_STATUS_ID, Constants.Companion.MeetingResultsStatus.NO_MEETING)
    }

    private fun addPagerFragments() {
        if (adapter == null) {
            adapter = MyPagerAdapter(supportFragmentManager)
        }
        if (!adapter?.addFragments(MeetingEditContactFragment.newInstance(meetingReportModel!!))!!) {
            throw Throwable("Failed to add Decision Maker Fragment")
        }
        if (!adapter?.addFragments(MeetingEditSummaryFragment.newInstance(meetingReportModel!!))!!) {
            throw Throwable("Failed to add Summary Fragment")
        }
        if (!adapter?.addFragments(MeetingEditReviewFragment.newInstance())!!) {
            throw Throwable("Failed to add Review Fragment")
        }
    }


    private fun zoomOutTransformation(page: View, position: Float) {
        when {
            position < -1 ->
                page.alpha = 0f
            position <= 1 -> {
                page.scaleX = Math.max(MeetingEditActivity.MIN_SCALE, 1 - Math.abs(position))
                page.scaleY = Math.max(MeetingEditActivity.MIN_SCALE, 1 - Math.abs(position))
                page.alpha = Math.max(MeetingEditActivity.MIN_ALPHA, 1 - Math.abs(position))
            }
            else -> page.alpha = 0f
        }
    }

    // MeetingEditContactFragment.OnMeetingEditContactSetListener implementation
    override fun onMeetingEditContactSet(decisionMakers: List<DecisionMakerModel>) {
        businessDetailsParams.put(Constants.DECISION_MAKERS, decisionMakers)
        moveToNextPage()
    }

    // MeetingEditSummaryFragment.OnMeetingEditSummarySetListener implementation
    override fun onMeetingEditSummarySet(params: java.util.HashMap<String, Any>) {
        businessDetailsParams.putAll(params)
        moveToNextPage()
    }

    // MeetingEditReviewFragment.OnMeetingEditConfirmReviewListener implementation
    override fun onMeetingEditConfirmReview(confirm: Boolean) {
        if (!confirm) {
            setResult(Activity.RESULT_CANCELED)
            onCancelPressed()
        } else {
            val meetingId: Int = meetingReportModel?.id ?: 0
            val noteId: Int = meetingReportModel?.note_id ?: 0
            val placeId: String = meetingReportModel?.placeId ?: ""
            val businessName: String = meetingReportModel?.getBusinessName() ?: ""
            val businessAddress: String = meetingReportModel?.getBusinessAddress() ?: ""
            val meetingStatusId: Int = businessDetailsParams.get(Constants.MEETING_STATUS_PARAM) as? Int ?: 1

            var reminder: String? = null
            if (businessDetailsParams.containsKey(Constants.MEETING_REMINDER) && businessDetailsParams.get(Constants.MEETING_REMINDER).toString().trim().isNotEmpty()) {
                reminder =
                    CalendarUtils.getCurrentLocalTimeString(businessDetailsParams.get(Constants.MEETING_REMINDER) as Long)
            }
            var note: String? = null
            if (businessDetailsParams.containsKey(Constants.MEETING_REASON)) {
                note = businessDetailsParams.get(Constants.MEETING_REASON) as? String
            }
            var contacts: List<DecisionMakerModel>? = null
            if (businessDetailsParams.containsKey(Constants.DECISION_MAKERS)) {
                contacts =
                    businessDetailsParams.get(Constants.DECISION_MAKERS) as? List<DecisionMakerModel> ?: arrayListOf()
            }
            var seeTheBill: Boolean? = null
            if (businessDetailsParams.containsKey(Constants.MEETING_SEE_THE_BILL)) {
                seeTheBill = businessDetailsParams.get(Constants.MEETING_SEE_THE_BILL) as? Boolean
            }
            if (contacts == null || seeTheBill == null || meetingStatusId == 1) {
                Toast.makeText(
                    context,
                    "Missing Data Please navigate back and complete the missing data.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            viewModel?.storeUpdateMeeting(
                meetingId, placeId, businessName, businessAddress, meetingStatusId,
                reminder, noteId, note, seeTheBill, contacts
            )
            setResult(Activity.RESULT_OK)
            onCancelPressed()
        }

    }

    // MeetingEditReviewFragment.OnMeetingEditConfirmReviewListener implementation
    override fun sendDataToFragment(fragment: MeetingEditReviewFragment) {
        fragment.putArguments(businessDetailsParams)
    }

    private fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                firstDotImageView.setImageResource(R.drawable.meeting_contact_on)
                secondDotImageView.setImageResource(R.drawable.meeting_summary_off)
                thirdDotImageView.setImageResource(R.drawable.meeting_review_off)
            }
            1 -> {
                firstDotImageView.setImageResource(R.drawable.meeting_contact_off)
                secondDotImageView.setImageResource(R.drawable.meeting_summary_on)
                thirdDotImageView.setImageResource(R.drawable.meeting_review_off)
            }
            2 -> {
                firstDotImageView.setImageResource(R.drawable.meeting_contact_off)
                secondDotImageView.setImageResource(R.drawable.meeting_summary_off)
                thirdDotImageView.setImageResource(R.drawable.meeting_review_on)
            }
        }
    }


    private fun moveToNextPage() {
        if (viewPager.currentItem < adapter?.count!!) {
            viewPager.setCurrentItem(viewPager.currentItem + 1, true)
        }
    }

    private fun showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.newInstance("Loading...")
        }
        progressDialog!!.show(supportFragmentManager, progressDialog!!::class.java.simpleName)
    }

    private fun clearSaveData() {
        businessDetailsParams.clear()
    }

    private fun onCancelPressed() {
        clearSaveData()
        finish()
        overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
    }

    override fun onBackPressed() {
        if (viewPager.currentItem != 0) {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
        } else {
            onCancelPressed()
        }
    }


    inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private var fragmentList: MutableList<Fragment> = ArrayList()

        override fun getItem(position: Int) = fragmentList[position]

        override fun getCount() = fragmentList.size

        fun addFragments(fragment: Fragment) = fragmentList.add(fragment)
    }

}