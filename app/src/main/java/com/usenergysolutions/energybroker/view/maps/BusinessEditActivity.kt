package com.usenergysolutions.energybroker.view.maps

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.config.LocationHelper
import com.usenergysolutions.energybroker.model.DataWrapper
import com.usenergysolutions.energybroker.model.ExtendedBusinessInfoModel
import com.usenergysolutions.energybroker.model.PlaceTypeModel
import com.usenergysolutions.energybroker.model.UesPlaceModel
import com.usenergysolutions.energybroker.ui.ControllableEditTextView
import com.usenergysolutions.energybroker.utils.StringUtils
import com.usenergysolutions.energybroker.view.adapters.PlacesTypeAdapter
import com.usenergysolutions.energybroker.view.dialogs.ProgressDialog
import com.usenergysolutions.energybroker.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.fragment_business_details.*

class BusinessEditActivity : AppCompatActivity(), ControllableEditTextView.OnClickListener {
    private val TAG: String = "BusinessEditActivity"

    // Helpers
    private lateinit var context: Context
    private var placeId: String? = null

    // Places typeList Spinner helpers
    private var adapter: PlacesTypeAdapter? = null
    private var placeTypeSArray: ArrayList<PlaceTypeModel>? = null
    private var placeType: PlaceTypeModel? = null

    // User's input Helpers
    private var emailsList: MutableList<ControllableEditTextView> = arrayListOf()
    private var phonesList: MutableList<ControllableEditTextView> = arrayListOf()

    // Communication
    private lateinit var viewModel: MapViewModel
    private var progressDialog: ProgressDialog? = null

    // Place Storage
    private var businessInfoModel: ExtendedBusinessInfoModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.fragment_business_details)
        Log.d(TAG, "onCreate")

        addPlaceTitleTextView.setText(R.string.edit_business_title)
        addPlaceSubTitleTextView.setText(R.string.edit_business_sub_title)

        Crashlytics.log("$TAG onCreate")

        // Init Communication
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        context = this

        // Check whether Place Id sent by with the Intent
        placeId = intent?.extras?.getString(Constants.PLACE_ID, null)
        if (placeId == null) {
            finish()
            return
        }


        // Init types Spinner
        placeTypeSArray = LocationHelper.getPlacesTypesArray()
        adapter = PlacesTypeAdapter(context as AppCompatActivity, R.layout.add_place_spinner_item, placeTypeSArray!!)
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
                val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v?.windowToken, 0)
                return false
            }
        })

        spinnerButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                businessTypeSpinner.performClick()
            }
        })

        initPlace(placeId!!)

        nextButton.setOnClickListener {
            Log.d(TAG, "Save Button")
            val isValid: Boolean = validateBusinessDetails()
            if (isValid && viewModel != null) {

                val phonesArray: MutableList<String> = getPhones()
                var phonesStr = phonesArray[0]
                if (phonesArray.size > 1) {
                    phonesStr = StringUtils.stringArrayToString(phonesArray.toTypedArray())
                }

                val emailsArray: MutableList<String> = getEmails()
                var emailsStr: String = emailsArray[0]
                if (emailsArray.size > 1) {
                    emailsStr = StringUtils.stringArrayToString(emailsArray.toTypedArray())
                }

                viewModel.updatePlace(
                    placeId!!, businessNameET.text.toString(), businessAddressET.text.toString(), phonesStr,
                    emailsStr, placeType?.getTypeId()!!
                ).observe(this, object : Observer<DataWrapper<UesPlaceModel>> {
                    override fun onChanged(t: DataWrapper<UesPlaceModel>?) {
                        if (t?.data != null) {
                            Toast.makeText(
                                context,
                                "Business ${t.data?.businessName} updated successfully",
                                Toast.LENGTH_LONG
                            ).show()
//                            onBackPressed()
                            goToBusinessInfoScreen()
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to update business ${t?.throwable?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
            }
        }
    }

    private fun goToBusinessInfoScreen() {
        val intent = Intent(context, BusinessInfoActivity::class.java)
        intent.putExtra(Constants.PLACE_ID, placeId!!)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    private fun initPlace(placeId: String) {
        showLoadingDialog()
        viewModel.getPlaceData(placeId).observe(this@BusinessEditActivity,
            Observer<DataWrapper<ExtendedBusinessInfoModel>> {
                if (it?.data != null) {
                    businessInfoModel = it.data
                    progressDialog!!.dismiss()
                    updateData(businessInfoModel)
                } else {
                    Toast.makeText(context, "Failed to retrieve Place data", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun updateData(data: ExtendedBusinessInfoModel?) {
        if (data != null) {
            businessNameET.setText(data.getBusinessName())
            businessAddressET.setText(data.businessAddressStr)
            val phones: Array<String> = StringUtils.stringToStringArray(data.getBusinessPhone())
            val emails: Array<String> = StringUtils.stringToStringArray(data.getBusinessEmail())
            val placeTypeId: Int = LocationHelper.getTypeByTypeName(data.getType()!![0])
            val typeIndex: Int = LocationHelper.getPlaceTypeIndex(placeTypeId)
            businessTypeSpinner.setSelection(typeIndex, true)
            placeType = placeTypeSArray?.get(typeIndex)

            for (phone: String in phones) {
                addNewPhoneForm(phone)
            }
            for (email: String in emails) {
                addNewEmailForm(email)
            }
        }
    }

    private fun addNewPhoneForm(phone: String? = null) {
        val phoneForm: ControllableEditTextView = ControllableEditTextView(
            context,
            InputType.TYPE_CLASS_PHONE, this, phonesList.size,
            resources.getString(R.string.add_business_phone_hint)
        )
        if (phone != null) phoneForm.setText(phone)
        phonesList.add(phoneForm)
        businessPhoneLayout.addView(phoneForm)
        setFormsIcons(phonesList)
    }

    private fun addNewEmailForm(email: String? = null) {
        val emailForm: ControllableEditTextView = ControllableEditTextView(
            context,
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, this, emailsList.size,
            resources.getString(R.string.add_business_email_hint)
        )
        if (email != null) emailForm.setText(email)
        emailsList.add(emailForm)
        businessEmailLayout.addView(emailForm)
        setFormsIcons(emailsList)
    }

    private fun setFormsIcons(aList: MutableList<ControllableEditTextView>) {
        for (i in 0 until aList.size) {
            aList[i].changeIcon(i == (aList.size - 1), i)
        }
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

    private fun showLoadingDialog() {
        progressDialog?.dismiss()
        if (progressDialog == null) {
            progressDialog = ProgressDialog()
        }
        progressDialog!!.show(supportFragmentManager, progressDialog!!::class.java.simpleName)
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


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
        finish()
    }
}