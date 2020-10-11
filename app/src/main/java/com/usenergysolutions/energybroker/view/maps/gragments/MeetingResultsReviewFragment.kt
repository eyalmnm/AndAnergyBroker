package com.usenergysolutions.energybroker.view.maps.gragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.model.DecisionMakerModel
import com.usenergysolutions.energybroker.ui.DecisionMakerDisplayView
import com.usenergysolutions.energybroker.utils.CalendarUtils
import kotlinx.android.synthetic.main.fragment_meeting_results_review.*

// Ref: https://stackoverflow.com/questions/37949024/kotlin-typecastexception-null-cannot-be-cast-to-non-null-type-com-midsizemango

class MeetingResultsReviewFragment : Fragment() {
    private val TAG: String = "MeetingResultsReviewFrg"

    private var contactsList: List<DecisionMakerModel>? = null
    private var meetingResultStatus: Int? = null

    interface OnMeetingConfirmReviewListener {
        fun onMeetingConfirmReview(confirm: Boolean)
        fun sendDataToFragment(fragment: MeetingResultsReviewFragment)
    }

    private var listener: OnMeetingConfirmReviewListener? = null

    // Data Holder
    private var params: HashMap<String, Any>? = null

    // Static Creator
    companion object {

        fun newInstance(): MeetingResultsReviewFragment {
            return MeetingResultsReviewFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnMeetingConfirmReviewListener
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        userVisibleHint = false
        val view = inflater.inflate(com.usenergysolutions.energybroker.R.layout.fragment_meeting_results_review, null)
        return view
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            Log.d(TAG, "Visible")
            // if (listener != null) listener?.sendDataToFragment(this)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            Log.d(TAG, "Visible")
            if (listener != null) listener?.sendDataToFragment(this)
        } else {
            Log.d(TAG, "Hidden")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton.setOnClickListener {
            if (checkValidity()) {
                if (listener != null) {
                    listener?.onMeetingConfirmReview(true)
                }
            }
        }

        cancelButton.setOnClickListener {
            if (listener != null) {
                listener?.onMeetingConfirmReview(false)
            }
        }
    }

    private fun checkValidity(): Boolean {
//        if (meetingResultStatus == Constants.Companion.MeetingResultsStatus.COME_AGAIN.id && contactsList.isNullOrEmpty()) {
//            Toast.makeText(context, R.string.come_again_and_empty_contatcts, Toast.LENGTH_LONG).show()
//            return false
//        } else {
//            return true
//        }
        return true
    }

    // Used for invoking the saved data from the parent Activity
    // This Fragment created before data filled by the user and we
    // can not use the arguments come to the fragment upon creation
    fun putArguments(params: HashMap<String, Any>) {
        this.params = params
        refreshUi()
    }

    // Ref: https://stackoverflow.com/questions/37949024/kotlin-typecastexception-null-cannot-be-cast-to-non-null-type-com-midsizemango
    private fun refreshUi() {
        if (params != null) {
            contactsList =
                params?.get(Constants.DECISION_MAKERS) as? List<DecisionMakerModel>

            if (contactsList == null || contactsList?.isEmpty() == true) {
                meetingResultsReviewContactsContainer.visibility = View.GONE
            } else {
                meetingResultsReviewContactsContainer.visibility = View.VISIBLE
            }
            if (meetingResultsReviewContactsContainer.childCount > 0) {
                meetingResultsReviewContactsContainer.removeAllViews()
            }

            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topMargin = resources.getDimensionPixelOffset(R.dimen.spacing_1x)
            layoutParams.bottomMargin = resources.getDimensionPixelOffset(R.dimen.spacing_1x)

            if (contactsList != null) {
                contactsList?.forEach { model: DecisionMakerModel ->
                    val dView = DecisionMakerDisplayView(context!!)
                    dView.setIsDecisionMaker(model.isDecisionMaker!!)
                    dView.setName(model.name ?: "")
                    dView.setEmail(model.email ?: "")
                    dView.setPhone(model.phone ?: "")
                    dView.setComment(model.comment ?: "")
                    dView.setIsDecisionMaker(model.isDecisionMaker ?: 0)

                    meetingResultsReviewContactsContainer.addView(dView, layoutParams)
                }
            }

            meetingResultStatus = params?.get(Constants.MEETING_STATUS_PARAM) as? Int
            if (meetingResultStatus != null) {
                meetingSummaryReviewStatus.text = Constants.getMeetingResultsStatusName(meetingResultStatus!!)
            }

            if (params?.containsKey(Constants.MEETING_REMINDER)!! && params?.get(Constants.MEETING_REMINDER).toString().trim().isNotEmpty()) {
                meetingSummaryReviewReminder.visibility = View.VISIBLE
                meetingSummaryReviewReminder.text =
                    CalendarUtils.getCurrentLocalTimeString(params?.get(Constants.MEETING_REMINDER)!! as Long)
            } else {
                meetingSummaryReviewReminder.visibility = View.INVISIBLE
            }

            if (params?.containsKey(Constants.MEETING_SEE_THE_BILL)!!) {
                if (params?.get(Constants.MEETING_SEE_THE_BILL) as Boolean) {
                    didYouSeeTheBillAnswer.text = getString(R.string.meeting_see_the_bill)
                } else {
                    didYouSeeTheBillAnswer.text = getString(R.string.meeting_not_see_the_bill)
                }
            }

            if (params?.containsKey(Constants.MEETING_REASON)!!) {
                reviewReasonFormLayout.visibility = View.VISIBLE
                meetingResultsReviewReason.text = params?.get(Constants.MEETING_REASON) as String
            } else {
                reviewReasonFormLayout.visibility = View.GONE
            }
        }
    }
}