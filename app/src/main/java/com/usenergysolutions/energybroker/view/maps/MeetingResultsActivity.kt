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
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.model.DecisionMakerModel
import com.usenergysolutions.energybroker.model.ExtendedBusinessInfoModel
import com.usenergysolutions.energybroker.utils.CalendarUtils
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.view.maps.gragments.MeetingResultsContactFragment
import com.usenergysolutions.energybroker.view.maps.gragments.MeetingResultsReviewFragment
import com.usenergysolutions.energybroker.view.maps.gragments.MeetingResultsSummaryFragment
import com.usenergysolutions.energybroker.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.activity_meeting_results.*

// Ref: https://stackoverflow.com/questions/42963445/disable-option-on-soft-keyboard-android-programmatically

class MeetingResultsActivity : AppCompatActivity(),
    MeetingResultsContactFragment.OnMeetingResultsContactSetListener,
    MeetingResultsSummaryFragment.OnMeetingResultsSummarySetListener,
    MeetingResultsReviewFragment.OnMeetingConfirmReviewListener {
    private val TAG: String = "MeetingResultsActivity"

    companion object {
        private const val MIN_SCALE = 0.65f
        private const val MIN_ALPHA = 0.3f
    }

    // UI Helpers
    private var adapter: MyPagerAdapter? = null

    // Place Storage
    private var businessInfoModel: ExtendedBusinessInfoModel? = null

    // Data Helper
    private val businessDetailsParams: HashMap<String, Any> = HashMap()
    private lateinit var context: Context

    // Communication
    private var viewModel: MapViewModel? = null
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        // Screen Configuration
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_meeting_results)
        context = this

        Crashlytics.log("$TAG onCreate")

        // Init communication
        viewModel = ViewModelProviders.of(this@MeetingResultsActivity).get(MapViewModel::class.java)

        businessInfoModel = intent.extras.getParcelable<ExtendedBusinessInfoModel>(Constants.APP_DATA)
        if (businessInfoModel == null) {
            Log.e(TAG, "onCreate", Exception("Business Info Model is missing"))
            Crashlytics.log("$TAG inCreate businessInfoModel is null")
            onCancelPressed()
        } else {
            addPagerFragments()
            viewPager.adapter = adapter
            viewPager.setPageTransformer(true, this::zoomOutTransformation)
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {}
                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

                override fun onPageSelected(p0: Int) {
                    this@MeetingResultsActivity.onPageSelected(p0)
                }
            })
        }

        businessDetailsParams.put(Constants.MEETING_STATUS_ID, Constants.Companion.MeetingResultsStatus.NO_MEETING)
    }

    private fun addPagerFragments() {
        if (adapter == null) {
            adapter = MyPagerAdapter(supportFragmentManager)
        }
        if (!adapter?.addFragments(MeetingResultsContactFragment.newInstance(businessInfoModel!!))!!) {
            throw Throwable("Failed to add Decision Maker Fragment")
        }
        if (!adapter?.addFragments(MeetingResultsSummaryFragment.newInstance(businessInfoModel!!))!!) {
            throw Throwable("Failed to add Summary Fragment")
        }
        if (!adapter?.addFragments(MeetingResultsReviewFragment.newInstance())!!) {
            throw Throwable("Failed to add Review Fragment")
        }
    }

    private fun clearSaveData() {
        businessDetailsParams.clear()
    }

    private fun moveToNextPage() {
        if (viewPager.currentItem < adapter?.count!!) {
            viewPager.setCurrentItem(viewPager.currentItem + 1, true)
        }
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

    //MeetingResultsContactFragment.OnMeetingResultsContactSetListener
    override fun onMeetingResultsContactSet(decisionMakers: List<DecisionMakerModel>) { // , meetWithDecisionMaker: Boolean) {
        businessDetailsParams.put(Constants.DECISION_MAKERS, decisionMakers)
        //businessDetailsParams.put(Constants.MEET_DECISION_MAKER, meetWithDecisionMaker)
        moveToNextPage()
    }

    // MeetingResultsSummaryFragment.OnMeetingResultsSummarySetListener implementation
    override fun onMeetingResultsSummarySet(params: java.util.HashMap<String, Any>) {
        businessDetailsParams.putAll(params)
        moveToNextPage()
    }

    // MeetingResultsReviewFragment.OnMeetingConfirmReviewListener Implementation
    override fun onMeetingConfirmReview(confirm: Boolean) {
        if (!confirm) {
            setResult(Activity.RESULT_CANCELED)
            onCancelPressed()
        } else {
            val placeId: String = businessInfoModel?.getPlaceId() ?: ""
            val businessName: String = businessInfoModel?.getBusinessName() ?: ""
            val businessAddress: String = businessInfoModel?.getBusinessAddress() ?: ""
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
            viewModel?.storeMeetingNew(
                placeId,
                businessName,
                businessAddress,
                meetingStatusId,
                reminder,
                note,
                seeTheBill,
                contacts
            )
            setResult(Activity.RESULT_OK)
            onCancelPressed()
        }
    }

    // MeetingResultsReviewFragment.OnMeetingConfirmReviewListener Implementation
    override fun sendDataToFragment(fragment: MeetingResultsReviewFragment) {
        fragment.putArguments(params = businessDetailsParams)
    }


    private fun zoomOutTransformation(page: View, position: Float) {
        when {
            position < -1 ->
                page.alpha = 0f
            position <= 1 -> {
                page.scaleX = Math.max(MeetingResultsActivity.MIN_SCALE, 1 - Math.abs(position))
                page.scaleY = Math.max(MeetingResultsActivity.MIN_SCALE, 1 - Math.abs(position))
                page.alpha = Math.max(MeetingResultsActivity.MIN_ALPHA, 1 - Math.abs(position))
            }
            else -> page.alpha = 0f
        }
    }

    private fun showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.newInstance("Loading...")
        }
        progressDialog!!.show(supportFragmentManager, progressDialog!!::class.java.simpleName)
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

    // Disable option on soft-keyboard android programmatically
    // Ref: https://stackoverflow.com/questions/42963445/disable-option-on-soft-keyboard-android-programmatically
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN || event?.action == KeyEvent.ACTION_UP) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_UNKNOWN -> return true
                KeyEvent.KEYCODE_DPAD_RIGHT -> return true
                KeyEvent.KEYCODE_DPAD_LEFT -> return true
            }
        }
        return super.dispatchKeyEvent(event)
    }


    inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private var fragmentList: MutableList<Fragment> = ArrayList()

        override fun getItem(position: Int) = fragmentList[position]

        override fun getCount() = fragmentList.size

        fun addFragments(fragment: Fragment) = fragmentList.add(fragment)
    }
}