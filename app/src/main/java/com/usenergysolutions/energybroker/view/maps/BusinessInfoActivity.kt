package com.usenergysolutions.energybroker.view.maps

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.model.*
import com.usenergysolutions.energybroker.utils.CalendarUtils
import com.usenergysolutions.energybroker.utils.StringUtils
import com.usenergysolutions.energybroker.view.maps.adapters.BusinessHistoryAdapter
import com.usenergysolutions.energybroker.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.activity_business_info.*

// Ref: https://www.raywenderlich.com/367-android-recyclerview-tutorial-with-kotlin
// Ref: https://stackoverflow.com/questions/30239627/how-to-change-the-style-of-a-datepicker-in-android
// Ref: https://medium.com/@mrkotlin/butter-knife-with-kotlin-no-need-c23e31b38be
// Ref: https://www.journaldev.com/10096/android-viewpager-example-tutorial
// Ref: https://stackoverflow.com/questions/48225258/kotlin-merge-two-nullable-mutable-list
// Ref: https://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view


class BusinessInfoActivity : AppCompatActivity() {
    private val TAG: String = "BusinessInfoActivity"

    private val meetingResultsRequestCode: Int = 123
    private val addNewNoteRequestCode: Int = 124

    // Helpers
    private lateinit var context: Context
    private var placeId: String? = null
    private var dayName: String = CalendarUtils.getDayOfTheWeekName()
    private var afterMeeting: Boolean? = null

    // History List Helper
    private var businessHistoryAdapter: BusinessHistoryAdapter? = null
    private var businessHistoryList: MutableList<BusinessHistoryItem> = arrayListOf()

    // Contacts ViewPager Helpers
    private var contactsAdapter: ContactsPagerAdapter? = null
    private var contactsList: MutableList<DecisionMakerModel> = arrayListOf()

    // Communication
    private lateinit var mapViewModel: MapViewModel

    // Users Data
    private var hasMeeting: Boolean = false
    //    private var isClosed: Boolean = false
    private var currentDecisionMaker: DecisionMakerModel? = null
    private var meetingStatus = Constants.Companion.MeetingResultsStatus.NO_MEETING

    // Place Storage
    private var businessInfoModel: ExtendedBusinessInfoModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(R.layout.activity_business_info)
        Log.d(TAG, "onCreate")

        Crashlytics.log("$TAG onCreate")

        // Init Communication
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        context = this

        // Check whether Place Id sent by with the Intent
        placeId = intent?.extras?.getString(Constants.PLACE_ID, null)
        if (placeId == null) {
            finish()
            return
        }
        initPlace(placeId!!)

        // Bind UI Components
        businessOpenToggleButton.setOnCheckedChangeListener { _, isChecked ->
            meetingStatus =
                if (isChecked)
                    Constants.Companion.MeetingResultsStatus.FAILED
                else
                    Constants.Companion.MeetingResultsStatus.NO_MEETING

            meetingToggleButton.isEnabled = !isChecked
            Log.d(TAG, "businessOpenToggleButton checked: $isChecked")
        }

        meetingToggleButton.setOnCheckedChangeListener { _, isChecked ->
            hasMeeting = !isChecked
            Log.d(TAG, "meetingToggleButton checked: $isChecked")
            if (isChecked) {
                openMeetingResultsActivity()
            } else {
                meetingStatus = Constants.Companion.MeetingResultsStatus.NO_MEETING
            }
        }

        contactsAdapter = ContactsPagerAdapter(context)
        contactsViewPager.adapter = contactsAdapter
        contactsViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                currentDecisionMaker = contactsList[position]
                if (position == 0) {
                    arrowToRightImageView.visibility = View.VISIBLE
                    arrowToLeftImageView.visibility = View.GONE
                }
                if (position > 0 && position < contactsList.size - 1) {
                    arrowToRightImageView.visibility = View.VISIBLE
                    arrowToLeftImageView.visibility = View.VISIBLE
                }
                if (position == contactsList.size - 1) {
                    arrowToRightImageView.visibility = View.GONE
                    arrowToLeftImageView.visibility = View.VISIBLE
                }
                if (contactsList.size <= 1) {
                    arrowToRightImageView.visibility = View.GONE
                    arrowToLeftImageView.visibility = View.GONE
                }
            }
        })

        businessHistoryAdapter = BusinessHistoryAdapter(context, historyArray = businessHistoryList)
        businessInfoHistoryListView.adapter = businessHistoryAdapter
        businessInfoHistoryListView.onItemClickListener = (AdapterView.OnItemClickListener { _, _, position, _ ->
            val historyItem = businessHistoryList[position]
            if (historyItem.getType() == Constants.Companion.ItemType.MEETING) {
                val meetingId = historyItem.getId()
                if (!meetingId.isNullOrEmpty()) {
                    loadMeeting(meetingId)
                }
            }
        })

        switchView()

        notesTitleTextView.setOnClickListener {
            notesImageView.performClick()
        }

        notesImageView.setOnClickListener {
            val intent = Intent(context, AddNewNoteActivity::class.java)
            intent.putExtra(Constants.USE_PLACE_ID, businessName.text)
            startActivityForResult(intent, addNewNoteRequestCode)
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
        }

        saveButton.setOnClickListener {
            Log.d(TAG, "Save Button")
            when {
                afterMeeting == true -> onBackPressed()
                meetingStatus == Constants.Companion.MeetingResultsStatus.NO_MEETING -> Toast.makeText(
                    context,
                    R.string.yes_or_failed_selection_only,
                    Toast.LENGTH_LONG
                ).show()
                else -> saveClosedOrNoMeetingFailed() // saveClosedOrNoMeeting()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadHistory(placeId)
    }

    private fun loadHistory(placeId: String?) {
        showLoadingDialog()
        mapViewModel.getMeetingsAndNotesByPlaceId(placeId!!)
            .observe(this,
                Observer<DataWrapper<MeetingAndNotesModel>> { t ->
                    infoLoadingDialog.visibility = View.GONE
                    businessHistoryList.clear()
                    if (t?.data != null) {
                        val meetingAndNotesModel = t.data
                        if (meetingAndNotesModel != null) {
                            val meetings: MutableList<BusinessHistoryItem> =
                                meetingAndNotesModel.getMeetings() as? MutableList<BusinessHistoryItem> ?: arrayListOf()
                            val notes: MutableList<BusinessHistoryItem> =
                                meetingAndNotesModel.getNotes() as? MutableList<BusinessHistoryItem> ?: arrayListOf()
                            var allData: MutableList<BusinessHistoryItem>? =
                                meetings.let { list1 -> notes.let { list2 -> list1 + list2 } }.toMutableList()
                            if (!allData.isNullOrEmpty()) {
                                allData =
                                    allData.sortedWith(compareBy { it.getMostUpdatedDate() }) as? MutableList<BusinessHistoryItem>
                                allData?.reverse()
                                businessHistoryList.addAll(0, allData!!)
                            }
                        }
                    } else {
                        if (t?.throwable?.message!!.isNotEmpty()) {
                            Toast.makeText(
                                context,
                                "Failed to retrieve meetings " + t.throwable?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(context, "Meetings not found for this place", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    businessHistoryAdapter!!.notifyDataSetChanged()
                    switchView()
                })
    }

    private fun loadMeeting(meetingId: String) {
        showLoadingDialog()
        mapViewModel.getMeetingByMeetingId(meetingId).observe(this,
            Observer<DataWrapper<MeetingReportModel>> { t ->
                if (t?.data != null) {
                    infoLoadingDialog.visibility = View.GONE
                    openMeetingEditScreen(t.data!!)
                } else {
                    if (t?.throwable?.message!!.isNotEmpty()) {
                        Toast.makeText(
                            context,
                            "Failed to retrieve meeting " + t.throwable?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        businessHistoryAdapter!!.notifyDataSetChanged()
                        Toast.makeText(context, "Meeting not found for this Id", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
    }

    private fun openMeetingEditScreen(data: MeetingReportModel) {
        val intent = Intent(context, MeetingEditActivity::class.java)
        intent.putExtra(Constants.APP_DATA, data)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    private fun openMeetingResultsActivity() {
        Log.d(TAG, "openMeetingResultsActivity")
        val intent = Intent(context, MeetingResultsActivity::class.java)
        intent.putExtra(Constants.APP_DATA, businessInfoModel)
        startActivityForResult(intent, meetingResultsRequestCode)
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == meetingResultsRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                afterMeeting = true
                initPlace(placeId!!)
                Handler(Looper.getMainLooper()).postDelayed({ loadHistory(placeId) }, 200)
            }
            meetingToggleButton.isChecked = false
        } else if (requestCode == addNewNoteRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val note: String? = data?.getStringExtra(Constants.APP_DATA)
                if (!note.isNullOrEmpty() && !placeId.isNullOrEmpty()) {
                    storeNewNote(placeId!!, note)
                }
            }
        }
    }

    private fun storeNewNote(placeId: String, note: String) {
        showLoadingDialog()
        mapViewModel.addNote(placeId, note).observe(this,
            Observer<DataWrapper<NoteModel>> {
                infoLoadingDialog.visibility = View.GONE
                loadHistory(placeId)
            })
    }

    private fun saveClosedOrNoMeetingFailed() {
        showLoadingDialog()
        val businessName = businessInfoModel?.getBusinessName() ?: ""
        val businessAddress = businessInfoModel?.getBusinessAddress() ?: ""
        mapViewModel.storeClosedNoMeetingFailed(placeId!!, businessName, businessAddress, meetingStatus.id).observe(
            this,
            Observer<DataWrapper<MeetingReposeModel>> { t ->
                infoLoadingDialog.visibility = View.GONE
                if (t?.throwable == null) {
                    Toast.makeText(context, "Data stored successful", Toast.LENGTH_LONG).show()
                    onBackPressed()
                } else {
                    Toast.makeText(context, "Failed to retrieve meeting data", Toast.LENGTH_LONG).show()
                }
            })
    }


//    private fun Activity.hideKeyboard() {
//        hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
//    }

//    private fun Context.hideKeyboard(view: View) {
//        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//    }


    private fun initPlace(placeId: String) {
        showLoadingDialog()
        mapViewModel.getPlaceData(placeId).observe(this@BusinessInfoActivity,
            Observer<DataWrapper<ExtendedBusinessInfoModel>> {
                if (it?.data != null) {
                    businessInfoModel = it.data
                    if (!StringUtils.isNumber(placeId)) { // Google POI
                        mapViewModel.getContactsByPlaceId(placeId).observe(this@BusinessInfoActivity,
                            Observer<DataWrapper<List<DecisionMakerModel>>> { t ->
                                infoLoadingDialog.visibility = View.GONE
                                if (t?.throwable == null) {
                                    businessInfoModel?.contactsList = t?.data
                                } else {
                                    Toast.makeText(context, "Failed to retrieve Contacts data", Toast.LENGTH_LONG)
                                        .show()
                                    Crashlytics.log(
                                        Log.ERROR,
                                        "initPlace POI",
                                        "failed to retrieve contacts ${t.throwable?.message}"
                                    )
                                }
                                updateData(businessInfoModel)
                            })
                    } else {  // Ues Place
                        infoLoadingDialog.visibility = View.GONE
                        updateData(businessInfoModel)
                    }
                } else {
                    Toast.makeText(context, "Failed to retrieve Place data", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun updateData(data: ExtendedBusinessInfoModel?) {
        Log.d(TAG, "updateData ${data?.getBusinessPhone()}")
        Log.d(TAG, "updateData ${data?.getBusinessEmail()}")
        if (data != null) {
            businessName.text = data.getBusinessName()
            businessPhone.text = data.getBusinessPhone()
            if (data.getBusinessPhone().isNullOrEmpty()) {
                businessPhone.visibility = View.GONE
            } else {
                businessPhone.visibility = View.VISIBLE
                businessPhone.text = data.getBusinessPhone()
            }
            if (data.getBusinessEmail().isNullOrEmpty()) {
                businessEmail.visibility = View.GONE
            } else {
                businessEmail.visibility = View.VISIBLE
                businessEmail.text = data.getBusinessEmail()
            }
            var tempType = data.getType()?.get(0)
            if (tempType != null) {
                tempType = StringUtils.placeTypeAndTimeCleaner(tempType)
            }
            businessTypeName.text = tempType
//            val weekDay: Array<String>? = data.getOpeningHoursArray()
            val weekDay: Array<String>? = null
            var openingTime = "  -:- / -:-"
            if (weekDay != null) {
                for (i in 0 until weekDay.size) {
                    if (weekDay[i].contains(dayName, true)) {
                        openingTime = weekDay[i].substring(dayName.length + 2)
                        openingTime = StringUtils.placeTypeAndTimeCleaner(openingTime)
                        break
                    }
                }
            }
            businessOpen.text = resources.getString(R.string.business_info_open, openingTime)

            contactsList.clear()
            contactsList.addAll(data.getContacts() ?: arrayListOf())
            contactsAdapter?.notifyDataSetChanged()
            if (contactsList.size > 1) {
                arrowToRightImageView.visibility = View.VISIBLE
            } else {
                arrowToRightImageView.visibility = View.GONE
                arrowToLeftImageView.visibility = View.GONE
            }

        }
    }

    private fun showLoadingDialog() {
        infoLoadingDialog.visibility = View.VISIBLE
    }

    private fun switchView() {
        if (businessHistoryList.isEmpty()) {
            businessInfoEmptyMeetingsListImage.visibility = View.VISIBLE
            businessInfoHistoryListView.visibility = View.GONE
        } else {
            businessInfoEmptyMeetingsListImage.visibility = View.GONE
            businessInfoHistoryListView.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(context, MapActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(R.anim.activity_slide_out_rev, R.anim.activity_slide_in_rev)
    }

    inner class ContactsPagerAdapter(val context: Context) : PagerAdapter() {

        private var inflater: LayoutInflater = LayoutInflater.from(context)

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val contact: DecisionMakerModel = contactsList[position]
            val layout: ViewGroup =
                inflater.inflate(R.layout.view_decision_maker_short_display, container, false) as ViewGroup

            // Bind Components
            val nameTv: TextView = layout.findViewById(R.id.contactNameTv)
            val emailTv: TextView = layout.findViewById(R.id.contactEmailTv)
            val phoneTv: TextView = layout.findViewById(R.id.contactPhoneTv)
            val commentTv: TextView = layout.findViewById(R.id.contactCommentTv)

            // Insert Data
            nameTv.text = contact.name
            emailTv.text = contact.email
            phoneTv.text = contact.phone
            commentTv.text = contact.comment

            // Adda Layout to the container
            container.addView(layout)

            return layout
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View?)
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun getCount(): Int {
            return contactsList.size
        }

        // Ref: https://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view
        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }
}