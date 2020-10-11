package com.usenergysolutions.energybroker.view.maps.gragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.config.LocationHelper
import com.usenergysolutions.energybroker.model.PlaceTypeModel
import com.usenergysolutions.energybroker.ui.ControllableEditTextView
import com.usenergysolutions.energybroker.utils.StringUtils
import com.usenergysolutions.energybroker.view.adapters.PlacesTypeAdapter
import kotlinx.android.synthetic.main.fragment_business_details.*

class BusinessDetailsFragment : AddBusinessBaseFragment(), ControllableEditTextView.OnClickListener {
    private val TAG: String = "BusinessDetailsFragment"

    // Activity Fragment communication interface
    interface OnBusinessDetailsSaveListener {
        fun BusinessDetailsSave(params: HashMap<String, Any>)
    }

    private var listener: OnBusinessDetailsSaveListener? = null

    // Places typeList Spinner helpers
    private var adapter: PlacesTypeAdapter? = null
    private var placeTypeSArray: ArrayList<PlaceTypeModel>? = null
    private var placeType: PlaceTypeModel? = null

    // User's input Helpers
    private var emailsList: MutableList<ControllableEditTextView> = arrayListOf()
    private var phonesList: MutableList<ControllableEditTextView> = arrayListOf()

    // Static Creator
    companion object {

        // TODO Consider this creator
//        fun newInstance(businessInfo: ExtendedBusinessInfoModel): BusinessDetailsFragment {
//            val args = Bundle()
//            args.putParcelable(Constants.APP_DATA, businessInfo)
//            val fragment = BusinessDetailsFragment()
//            fragment.arguments = args
//            return  fragment
//        }

        fun newInstance(): BusinessDetailsFragment {
            return BusinessDetailsFragment()
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnBusinessDetailsSaveListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    // Used for invoking the saved data from the parent Activity
    // This Fragment created before data filled by the user and we
    // can not use the arguments come to the fragment upon creation
    fun putArguments(params: HashMap<String, Any>) {
        businessAddressET.setText(params[Constants.BUSINESS_ADDRESS] as? String)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_business_details, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init types Spinner
        placeTypeSArray = LocationHelper.getPlacesTypesArray()
        adapter = PlacesTypeAdapter(activity as AppCompatActivity, R.layout.add_place_spinner_item, placeTypeSArray!!)
        businessTypeSpinner.adapter = adapter
        businessTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                placeType = placeTypeSArray?.get(position)
            }
        }
//        businessTypeSpinner.setPopupBackgroundResource(android.R.color.holo_purple)
        placeType = placeTypeSArray?.get(0)
        businessTypeSpinner.setSelection(0, true)
        businessTypeSpinner.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v?.windowToken, 0)
                return false
            }
        })

        spinnerButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                businessTypeSpinner.performClick()
            }
        })

        // Dynamically add new email typeList Edit text
        // Add the current email and phone EditTexts to the arrays
        addNewEmailForm()
        addNewPhoneForm()

        nextButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val isValid: Boolean = validateBusinessDetails()
                if (isValid && listener != null) {
                    listener?.BusinessDetailsSave(createDataHashMap())
                }
            }
        })
    }

    private fun addNewEmailForm() {
        val emailForm: ControllableEditTextView = ControllableEditTextView(
            context!!,
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, this, emailsList.size,
            resources.getString(R.string.add_business_email_hint)
        )
        emailsList.add(emailForm)
        businessEmailLayout.addView(emailForm)
        setFormsIcons(emailsList)
    }

    private fun setFormsIcons(aList: MutableList<ControllableEditTextView>) {
        for (i in 0 until aList.size) {
            aList[i].changeIcon(i == (aList.size - 1), i)
        }
    }

    private fun addNewPhoneForm() {
        val phoneForm: ControllableEditTextView = ControllableEditTextView(
            context!!,
            InputType.TYPE_CLASS_PHONE, this, phonesList.size,
            resources.getString(R.string.add_business_phone_hint)
        )
        phonesList.add(phoneForm)
        businessPhoneLayout.addView(phoneForm)
        setFormsIcons(phonesList)
    }

    // ControllableEditTextView.OnClickListener implementation
    override fun onClick(parentView: View, index: Int, inputType: Int) {
        if (inputType == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
            if (index < (emailsList.size - 1)) {
                removeEmailFromBefore(index)
            } else {
                addNewEmailForm()
            }
        } else if (inputType == InputType.TYPE_CLASS_PHONE) {
            if (index < (phonesList.size - 1)) {
                removePhoneFormBefore(index)
            } else {
                addNewPhoneForm()
            }
        }
    }

    private fun removePhoneFormBefore(index: Int) {
        if (phonesList.size <= 1) return
        val form: ControllableEditTextView = phonesList.removeAt(index + 1)
        businessPhoneLayout.removeView(form)
        setFormsIcons(phonesList)
    }

    private fun removeEmailFromBefore(index: Int) {
        if (emailsList.size <= 1) return
        val form: ControllableEditTextView = emailsList.removeAt(index + 1)
        businessEmailLayout.removeView(form)
        setFormsIcons(emailsList)
    }

    private fun createDataHashMap(): HashMap<String, Any> {
        val params: HashMap<String, Any> = HashMap()
        params.put(Constants.BUSINESS_NAME, businessNameET.text.toString())
        params.put(Constants.BUSINESS_ADDRESS, businessAddressET.text.toString())

        val emailsArray: MutableList<String> = getEmails()
        var emailsStr: String = emailsArray[0]
        if (emailsArray.size > 1) {
            emailsStr = StringUtils.stringArrayToString(emailsArray.toTypedArray())
        }
        params.put(Constants.BUSINESS_EMAIL, emailsStr)

        val phonesArray: MutableList<String> = getPhones()
        var phonesStr = phonesArray[0]
        if (phonesArray.size > 1) {
            phonesStr = StringUtils.stringArrayToString(phonesArray.toTypedArray())
        }
        params.put(Constants.BUSINESS_PHONE, phonesStr)

        params.put(Constants.BUSINESS_TYPE, placeType?.getTypeId()!!)
        return params
    }

    private fun getPhones(): MutableList<String> {
        val phones: MutableList<String> = arrayListOf()
        for (form: ControllableEditTextView in phonesList) {
            phones.add(form.getText()!!)
        }
        return phones
    }

    private fun getEmails(): MutableList<String> {
        val emails: MutableList<String> = arrayListOf()
        for (form: ControllableEditTextView in emailsList) {
            emails.add(form.getText()!!)
        }
        return emails
    }

    private fun validateBusinessDetails(): Boolean {

        if (businessNameET.text.toString().isEmpty() || businessAddressET.text.toString().isEmpty()) {
            Toast.makeText(context, "Missing Data! Please fill all the fields in the form", Toast.LENGTH_LONG).show()
            return false
        }

        // Until we add option to remove un-necessary empty EditText
        for (form: ControllableEditTextView in emailsList) {
            if (form.getText()!!.isNotEmpty()) {
                if (!StringUtils.isValidEmail(form.getText()!!)) {
                    Toast.makeText(context, "Invalid email address: ${form.getText()!!}", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }

        // Until we add option to remove un-necessary empty EditText
        for (form: ControllableEditTextView in phonesList) {
            if (form.getText()!!.isNotEmpty()) {
                if (!StringUtils.isValidPhoneNumber(form.getText()!!)) {
                    Toast.makeText(context, "Invalid phone address: ${form.getText()!!}", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }

        return true
    }

    // From Base Fragment
    override fun saveData() {
        val isValid: Boolean = validateBusinessDetails()
        if (isValid && listener != null) {
            listener?.BusinessDetailsSave(createDataHashMap())
        }
    }
}