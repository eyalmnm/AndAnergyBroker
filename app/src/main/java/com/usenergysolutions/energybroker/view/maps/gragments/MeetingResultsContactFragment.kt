package com.usenergysolutions.energybroker.view.maps.gragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.model.DecisionMakerModel
import com.usenergysolutions.energybroker.model.ExtendedBusinessInfoModel
import com.usenergysolutions.energybroker.ui.DecisionMakerView
import com.usenergysolutions.energybroker.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_meeting_results_contact.*

class MeetingResultsContactFragment : Fragment(), DecisionMakerView.OnClickListener {
    private val TAG: String = "MeetingResultsContFrag"

    interface OnMeetingResultsContactSetListener {
        fun onMeetingResultsContactSet(decisionMakers: List<DecisionMakerModel>) //, meetWithDecisionMaker: Boolean)
    }

    private var listener: OnMeetingResultsContactSetListener? = null

    private var businessInfoModel: ExtendedBusinessInfoModel? = null

    // Decision Makers Forms holder
    private var decisionMakerList: MutableList<DecisionMakerView> = arrayListOf()
    private val layoutParams: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    // Static Creator
    companion object {

        fun newInstance(businessInfoModel: ExtendedBusinessInfoModel): MeetingResultsContactFragment {
            val args = Bundle()
            args.putParcelable(Constants.APP_DATA, businessInfoModel)
            val fragment = MeetingResultsContactFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnMeetingResultsContactSetListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_meeting_results_contact, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        businessInfoModel = arguments?.getParcelable(Constants.APP_DATA)
        var decisionMakerModels: MutableList<DecisionMakerModel>? = null
        if (businessInfoModel?.getContacts() != null) {
            decisionMakerModels = businessInfoModel?.getContacts() as? MutableList<DecisionMakerModel>
        }
        decisionMakerList.clear()
        if (decisionMakerModels != null && decisionMakerModels.size > 0) {
            for (dmModel: DecisionMakerModel in decisionMakerModels) {
                val dmView = DecisionMakerView(context!!, this@MeetingResultsContactFragment, 0)
                dmView.setDecisionMaker(dmModel)
                contactsFormContainerLayout.addView(dmView, layoutParams)
                decisionMakerList.add(dmView)
            }
        } else {
            addForm()
        }
        setFormsIcons(decisionMakerList)
        mainScrollView.fullScroll(View.FOCUS_DOWN)

        nextBtn.setOnClickListener { sandData() }

        meetingFragmentAddDecisionMakerTextView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                meetingFragmentAddDecisionMakerIcon.performClick()
            }
        })

        meetingFragmentAddDecisionMakerIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                addForm()
            }
        })
        switchView()
    }

    // MeetingDecisionMakerView.OnClickListener implementation
    override fun onClick(parentView: View, index: Int) {
        if (index >= (decisionMakerList.size - 1)) {
            addForm()
        } else {
            if (decisionMakerList.size <= 1) return
            val form: DecisionMakerView? = decisionMakerList.removeAt(index + 1)
            contactsFormContainerLayout.removeView(form)
        }
        setFormsIcons(decisionMakerList)
        mainScrollView.fullScroll(View.FOCUS_DOWN)
        switchView()
    }

    // DecisionMakerView.notDecisionMaker implementation
    override fun notDecisionMaker(index: Int) {
        val form: DecisionMakerView = decisionMakerList.removeAt(index)
        contactsFormContainerLayout.removeView(form)
        setFormsIcons(decisionMakerList)
        mainScrollView.fullScroll(View.FOCUS_DOWN)
        if (decisionMakerList.isEmpty()) {
            sandData()
        }
        switchView()
    }

    private fun setFormsIcons(decisionMakerList: MutableList<DecisionMakerView>) {
        for (i in 0 until decisionMakerList.size) {
            decisionMakerList[i].changeIcon(i == (decisionMakerList.size - 1), i)
        }
    }

    private fun sandData() {
        if (listener != null) {
            val retArray: MutableList<DecisionMakerModel> = arrayListOf()
            for (decisionMaker: DecisionMakerView in decisionMakerList) {
                val model: DecisionMakerModel = decisionMaker.getDecisionMaker()

                if (model.name.isNullOrEmpty()) {
                    showToast("Contact name left empty!")
                    return
                }

                /*if (model.email.isNullOrEmpty()) {
                    showToast("${model.name} email address left empty!")
                    return
                } else*/
                if (!model.email.isNullOrEmpty() && !StringUtils.isValidEmail(model.email)) {
                    showToast("${model.name} email address is not valid!")
                    return
                }

                /*if (model.phone.isNullOrEmpty()) {
                    showToast("${model.name} phone number left empty!")
                    return
                } else*/
                if (!model.phone.isNullOrEmpty() && !StringUtils.isValidPhoneNumber(model.phone)) {
                    showToast("${model.name} phone number is not valid!")
                    return
                }
                retArray.add(model)
            }
            listener?.onMeetingResultsContactSet(retArray)
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    private fun addForm() {
        var decisionMakerView =
            DecisionMakerView(context!!, this@MeetingResultsContactFragment, decisionMakerList.size)
        contactsFormContainerLayout.addView(decisionMakerView, layoutParams)
        decisionMakerList.add(decisionMakerView)
        switchView()
    }

    private fun switchView() {
        if (decisionMakerList.isNullOrEmpty()) {
            addContactFormLayout.visibility = View.VISIBLE
            contactsFormContainerLayout.visibility = View.GONE
        } else {
            addContactFormLayout.visibility = View.GONE
            contactsFormContainerLayout.visibility = View.VISIBLE
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}