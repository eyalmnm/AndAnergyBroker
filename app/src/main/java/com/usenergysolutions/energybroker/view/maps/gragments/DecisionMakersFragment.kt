package com.usenergysolutions.energybroker.view.maps.gragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.model.DecisionMakerModel
import com.usenergysolutions.energybroker.ui.DecisionMakerView
import com.usenergysolutions.energybroker.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_decision_makers.*

class DecisionMakersFragment : AddBusinessBaseFragment(), DecisionMakerView.OnClickListener {
    private val TAG: String = "DecisionMakersFragment"

    interface OnDecisionMakerSetListener {
        fun onDecisionMakerSet(decisionMakers: List<DecisionMakerModel>)
    }

    private var listener: OnDecisionMakerSetListener? = null

    // Decision Makers Forms holder
    private var decisionMakerList: MutableList<DecisionMakerView> = arrayListOf()
    private val layoutParams: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


    // Static Creator
    companion object {

        fun newInstance(): DecisionMakersFragment {
            return DecisionMakersFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnDecisionMakerSetListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater.inflate(R.layout.fragment_decision_makers, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addForm()

        nextButton.setOnClickListener { sendData() }
    }

    // DecisionMakerView.OnClickListener implementation
    override fun onClick(parentView: View, index: Int) {
        if (index >= (decisionMakerList.size - 1)) {
            addForm()
        } else {
            if (decisionMakerList.size <= 1) return
            val form: DecisionMakerView = decisionMakerList.removeAt(index + 1)
            contactsFormContainer.removeView(form)
        }
        setFormsIcons(decisionMakerList)
        scrollView.fullScroll(View.FOCUS_DOWN)
    }

    // DecisionMakerView.notDecisionMaker implementation
    override fun notDecisionMaker(index: Int) {
        val form: DecisionMakerView = decisionMakerList.removeAt(index)
        contactsFormContainer.removeView(form)
        setFormsIcons(decisionMakerList)
        scrollView.fullScroll(View.FOCUS_DOWN)
    }

    private fun setFormsIcons(decisionMakerList: MutableList<DecisionMakerView>) {
        for (i in 0 until decisionMakerList.size) {
            decisionMakerList[i].changeIcon(i == (decisionMakerList.size - 1), i)
        }
    }

    private fun sendData() {
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
            listener?.onDecisionMakerSet(retArray)
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    private fun addForm() {
        var decisionMakerView = DecisionMakerView(context!!, this, decisionMakerList.size)
        contactsFormContainer.addView(decisionMakerView, layoutParams)
        decisionMakerList.add(decisionMakerView)
    }

    override fun saveData() {
        sendData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        decisionMakerList.clear()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}