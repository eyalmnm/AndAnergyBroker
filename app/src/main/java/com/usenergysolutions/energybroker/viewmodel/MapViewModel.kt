package com.usenergysolutions.energybroker.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.usenergysolutions.energybroker.BuildConfig
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.application.EnergyBrokerApp
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.config.Dynamic
import com.usenergysolutions.energybroker.model.*
import com.usenergysolutions.energybroker.model.converters.ToExtendedBusinessInfoModel
import com.usenergysolutions.energybroker.model.incoming.map.*
import com.usenergysolutions.energybroker.model.incoming.map.google.PlaceData
import com.usenergysolutions.energybroker.model.outgoing.map.*
import com.usenergysolutions.energybroker.remote.ApiController
import com.usenergysolutions.energybroker.remote.MapApi
import com.usenergysolutions.energybroker.utils.CalendarUtils
import com.usenergysolutions.energybroker.utils.RetrofitUtils
import com.usenergysolutions.energybroker.utils.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Ref: https://www.androidtutorialpoint.com/intermediate/google-maps-nearby-places-api-using-retrofit-android/

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = "MapViewModel"

    private var mapApi: MapApi? = null

    init {
        mapApi = ApiController.createService(MapApi::class.java)
    }

    fun getNotes(placeId: String): LiveData<DataWrapper<List<NoteModel>>> {
        val liveData: MutableLiveData<DataWrapper<List<NoteModel>>> = MutableLiveData()
        val dataWrapper: DataWrapper<List<NoteModel>> = DataWrapper()

        mapApi?.getNotesByPlaceId(BuildConfig.APP_TOKEN, PlaceDataById(Dynamic.uuid!!, placeId))
            ?.enqueue(object : Callback<GetNotesByPlaceResponse> {
                override fun onFailure(call: Call<GetNotesByPlaceResponse>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(
                    call: Call<GetNotesByPlaceResponse>,
                    response: Response<GetNotesByPlaceResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.error == true) {
                            dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                        } else {
                            dataWrapper.data = response.body()?.notes
                        }
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }

    fun addNote(placeId: String, noteText: String): LiveData<DataWrapper<NoteModel>> {
        val liveData: MutableLiveData<DataWrapper<NoteModel>> = MutableLiveData()
        val dataWrapper: DataWrapper<NoteModel> = DataWrapper()

        mapApi?.addNote(
            BuildConfig.APP_TOKEN,
            AddNote(Dynamic.uuid!!, placeId, noteText, CalendarUtils.getCurrentLocalTimeString())
        )?.enqueue(object : Callback<AddNoteResponse> {
            override fun onFailure(call: Call<AddNoteResponse>, t: Throwable) {
                dataWrapper.throwable = t
                liveData.value = dataWrapper
            }

            override fun onResponse(call: Call<AddNoteResponse>, response: Response<AddNoteResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.error == true) {
                        dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                    } else {
                        dataWrapper.data = response.body()?.noteModel
                    }
                } else {
                    dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                }
                liveData.value = dataWrapper
            }
        })

        return liveData
    }

    fun addBusiness(
        latitude: Double, longitude: Double, name: String, address: String, email: String, phone: String, type: Int,
        contacts: List<DecisionMakerModel>, openingHours: List<String>?
    ): LiveData<DataWrapper<UesPlaceModel>> {
        val liveData: MutableLiveData<DataWrapper<UesPlaceModel>> = MutableLiveData()
        val dataWrapper: DataWrapper<UesPlaceModel> = DataWrapper()

        val openingHoursString: String = StringUtils.stringArrayToString(openingHours?.toTypedArray())
        val contactsJSONArrayString: String = getArrayString(contacts)

        mapApi?.addBusiness(
            BuildConfig.APP_TOKEN, AddBusinessModel(
                Dynamic.uuid!!,
                latitude,
                longitude,
                name,
                address,
                email,
                phone,
                type,
                contactsJSONArrayString,
                openingHoursString,
                CalendarUtils.getCurrentLocalTimeString()
            )
        )?.enqueue(object : Callback<UpdatePlaceResponse> {
            override fun onFailure(call: Call<UpdatePlaceResponse>, t: Throwable) {
                dataWrapper.throwable = t
                liveData.value = dataWrapper
            }

            override fun onResponse(call: Call<UpdatePlaceResponse>, response: Response<UpdatePlaceResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.error == true) {
                        dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                    } else {
                        dataWrapper.data = response.body()?.place
                    }
                } else {
                    dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                }
                liveData.value = dataWrapper
            }
        })

        return liveData
    }

    fun updatePlace(
        placeId: String,
        name: String,
        address: String,
        phone: String,
        email: String,
        type: Int
    ): LiveData<DataWrapper<UesPlaceModel>> {
        val liveData: MutableLiveData<DataWrapper<UesPlaceModel>> = MutableLiveData()
        val dataWrapper: DataWrapper<UesPlaceModel> = DataWrapper()

        mapApi?.updatePlace(
            BuildConfig.APP_TOKEN, UpdatePlaceModel(
                Dynamic.uuid!!, placeId, name, address, phone, email,
                type, CalendarUtils.getCurrentLocalTimeString()
            )
        )?.enqueue(object : Callback<UpdatePlaceResponse> {
            override fun onFailure(call: Call<UpdatePlaceResponse>, t: Throwable) {
                dataWrapper.throwable = t
                liveData.value = dataWrapper
            }

            override fun onResponse(call: Call<UpdatePlaceResponse>, response: Response<UpdatePlaceResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.error == true) {
                        dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                    } else {
                        dataWrapper.data = response.body()?.place
                    }
                } else {
                    dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                }
                liveData.value = dataWrapper
            }
        })

        return liveData
    }

    private fun getArrayString(contacts: List<DecisionMakerModel>?): String {
        if (contacts == null) return ""
        val contactsStr: MutableList<String> = arrayListOf()
        for (contact: DecisionMakerModel in contacts) {
            contactsStr.add(contact.getAsString())
        }
        return StringUtils.stringArrayToString(contactsStr.toTypedArray(), Constants.CONTACTS_DELIMITER)
    }

    fun getNearByPlaces(latitude: Double, longitude: Double): LiveData<DataWrapper<NearByPlacesResponse>> {
        val liveData: MutableLiveData<DataWrapper<NearByPlacesResponse>> = MutableLiveData()
        val dataWrapper: DataWrapper<NearByPlacesResponse> = DataWrapper()

        mapApi?.getNearByPlaces(BuildConfig.APP_TOKEN, GetNearByPlaces(Dynamic.uuid!!, latitude, longitude))
            ?.enqueue(object : Callback<NearByPlacesResponse> {
                override fun onFailure(call: Call<NearByPlacesResponse>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(call: Call<NearByPlacesResponse>, response: Response<NearByPlacesResponse>) {
                    if (response.isSuccessful) {
                        dataWrapper.data = response.body()
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }

    fun getPlaceData(placeId: String): LiveData<DataWrapper<ExtendedBusinessInfoModel>> {
        return if (StringUtils.isNumber(placeId)) {
            getUesPlaceDataById(placeId)
        } else {
            getPoiData(placeId)
        }
    }

    private fun getUesPlaceDataById(placeId: String): LiveData<DataWrapper<ExtendedBusinessInfoModel>> {
        val liveData: MutableLiveData<DataWrapper<ExtendedBusinessInfoModel>> = MutableLiveData()
        val dataWrapper: DataWrapper<ExtendedBusinessInfoModel> = DataWrapper()

        mapApi?.getUesPlaceDataById(BuildConfig.APP_TOKEN, PlaceDataById(Dynamic.uuid!!, placeId))
            ?.enqueue(object : Callback<UesPlaceDataByIdResponse> {
                override fun onFailure(call: Call<UesPlaceDataByIdResponse>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(
                    call: Call<UesPlaceDataByIdResponse>,
                    response: Response<UesPlaceDataByIdResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.place != null) {
                            dataWrapper.data =
                                ToExtendedBusinessInfoModel.getExtendedBusinessInfoModel(response.body()?.place!!)
                        } else {
                            dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                        }
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }

    // Ref: https://www.androidtutorialpoint.com/intermediate/google-maps-nearby-places-api-using-retrofit-android/
    fun getPoiData(placeId: String): LiveData<DataWrapper<ExtendedBusinessInfoModel>> {
        val liveData: MutableLiveData<DataWrapper<ExtendedBusinessInfoModel>> = MutableLiveData()
        val dataWrapper: DataWrapper<ExtendedBusinessInfoModel> = DataWrapper()

        mapApi?.getPoiData(placeId, EnergyBrokerApp.getAppContext().getString(R.string.google_maps_key))
            ?.enqueue(object : Callback<PlaceData> {
                override fun onFailure(call: Call<PlaceData>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(call: Call<PlaceData>, response: Response<PlaceData>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            val businessInfoModel: ExtendedBusinessInfoModel? =
                                ToExtendedBusinessInfoModel.getExtendedBusinessInfoModel(
                                    ModelsFactory.createBusinessInfoModel(response.body())!!
                                )
                            dataWrapper.data = businessInfoModel
                        } else {
                            dataWrapper.throwable = Throwable("Failed to retrieve place data!")
                        }
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }

    fun getContactsByPlaceId(placeId: String): LiveData<DataWrapper<List<DecisionMakerModel>>> {
        val liveData: MutableLiveData<DataWrapper<List<DecisionMakerModel>>> = MutableLiveData()
        val dataWrapper: DataWrapper<List<DecisionMakerModel>> = DataWrapper()

        mapApi?.getContactsByPlaceId(BuildConfig.APP_TOKEN, PlaceDataById(Dynamic.uuid!!, placeId))
            ?.enqueue(object : Callback<GetContactsResponse> {
                override fun onFailure(call: Call<GetContactsResponse>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(call: Call<GetContactsResponse>, response: Response<GetContactsResponse>) {
                    if (response.isSuccessful) {
                        if (response.body()?.error == false) {
                            dataWrapper.data = response.body()?.contacts
                        } else {
                            dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                        }
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }

    fun storeClosedNoMeetingFailed(
        placeId: String,
        businessName: String,
        businessAddress: String,
        meetingStatusId: Int
    ): LiveData<DataWrapper<MeetingReposeModel>> {
        return storeMeetingNew(
            placeId, businessName, businessAddress, meetingStatusId, null,
            null, null, null
        )
    }


    fun storeMeetingNew(
        placeId: String, businessName: String, businessAddress: String, meetingStatusId: Int, reminder: String?,
        note: String?, seeTheBill: Boolean?, contacts: List<DecisionMakerModel>?
    ): LiveData<DataWrapper<MeetingReposeModel>> {
        val liveData: MutableLiveData<DataWrapper<MeetingReposeModel>> = MutableLiveData()
        val dataWrapper: DataWrapper<MeetingReposeModel> = DataWrapper()

        //val re = Regex("[^A-Za-z0-9 ]")
        var safeBusinessName = if (businessName.isNullOrEmpty()) "No Name" else businessName
        val contactsJSONArrayString: String = getArrayString(contacts)
        val safeReminder: String = reminder ?: ""
        val safeNote: String = note ?: ""
        val safeSeeTheBill = if (seeTheBill == true) 1 else 0

        mapApi?.storeClosedNoMeetingFailed(
            BuildConfig.APP_TOKEN, StoreMeeting(
                Dynamic.uuid!!,
                placeId,
                safeBusinessName,
                businessAddress,
                meetingStatusId,
                safeNote,
                safeReminder,
                contactsJSONArrayString,
                safeSeeTheBill,
                CalendarUtils.getCurrentLocalTimeString()
            )
        )?.enqueue(object : Callback<StoreMeetingResponse> {
            override fun onFailure(call: Call<StoreMeetingResponse>, t: Throwable) {
                dataWrapper.throwable = t
                liveData.value = dataWrapper
            }

            override fun onResponse(call: Call<StoreMeetingResponse>, response: Response<StoreMeetingResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        dataWrapper.data = response.body()?.meetingResponse
                    } else {
                        dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                    }
                } else {
                    dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                }
                liveData.value = dataWrapper
            }
        })

        return liveData
    }

    fun storeUpdateMeeting(
        meetingId: Int, placeId: String, businessName: String, businessAddress: String,
        meetingStatusId: Int, reminder: String?, noteId: Int?, note: String?, seeTheBill: Boolean?,
        contacts: List<DecisionMakerModel>?
    ): LiveData<DataWrapper<MeetingReposeModel>> {
        val liveData: MutableLiveData<DataWrapper<MeetingReposeModel>> = MutableLiveData()
        val dataWrapper: DataWrapper<MeetingReposeModel> = DataWrapper()

        val contactsJSONArrayString: String = getArrayString(contacts)
        val safeReminder: String = reminder ?: ""
        val safeNoteId: Int = noteId ?: 0
        var safeNote: String = note ?: ""
        if (safeNote.equals("null", true)) safeNote = ""
        val seeTheBillInt: Int = if (seeTheBill == true) 1 else 0

        val updateMeetingModel: UpdateMeetingModel = UpdateMeetingModel(
            Dynamic.uuid!!,
            meetingId,
            placeId,
            businessName,
            businessAddress,
            safeNoteId,
            safeNote,
            safeReminder,
            meetingStatusId,
            contactsJSONArrayString,
            seeTheBillInt,
            CalendarUtils.getCurrentLocalTimeString()
        )

        mapApi?.updateMeetingNew(BuildConfig.APP_TOKEN, updateMeetingModel)
            ?.enqueue(object : Callback<StoreMeetingResponse> {
                override fun onFailure(call: Call<StoreMeetingResponse>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(call: Call<StoreMeetingResponse>, response: Response<StoreMeetingResponse>) {
                    if (response.isSuccessful) {
                        if (response.body()?.error == true) {
                            dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                        } else {
                            dataWrapper.data = response.body()?.meetingResponse
                        }
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }

    fun getMeetingsByPlaceId(placeId: String): LiveData<DataWrapper<List<MeetingModel>>> {
        val liveData: MutableLiveData<DataWrapper<List<MeetingModel>>> = MutableLiveData()
        val dataWrapper: DataWrapper<List<MeetingModel>> = DataWrapper()

        mapApi?.getMeetingsByPlaceId(BuildConfig.APP_TOKEN, PlaceDataById(Dynamic.uuid!!, placeId))
            ?.enqueue(object : Callback<GetMeetingsResponse> {
                override fun onFailure(call: Call<GetMeetingsResponse>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(call: Call<GetMeetingsResponse>, response: Response<GetMeetingsResponse>) {
                    if (response.isSuccessful) {
                        if (response.body()?.error == true) {
                            dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                        } else {
                            dataWrapper.data = response.body()?.meetings
                        }
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }

    fun getMeetingsAndNotesByPlaceId(placeId: String): LiveData<DataWrapper<MeetingAndNotesModel>> {
        val liveData: MutableLiveData<DataWrapper<MeetingAndNotesModel>> = MutableLiveData()
        val dataWrapper: DataWrapper<MeetingAndNotesModel> = DataWrapper()

        mapApi?.getMeetingsAndNotesByPlaceId(BuildConfig.APP_TOKEN, MeetingAndNotesByPlaceId(Dynamic.uuid!!, placeId))
            ?.enqueue(object : Callback<MeetingAndNotesResponse> {
                override fun onFailure(call: Call<MeetingAndNotesResponse>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(
                    call: Call<MeetingAndNotesResponse>,
                    response: Response<MeetingAndNotesResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.error == true) {
                            dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                        } else {
                            val meetingAndNotesModel = MeetingAndNotesModel()
                            meetingAndNotesModel.setMeeting(response.body()?.meetings)
                            meetingAndNotesModel.setMeetingCounter(response.body()?.meetings?.size)
                            meetingAndNotesModel.setNotes(response.body()?.notes)
                            meetingAndNotesModel.setNoteCounter(response.body()?.notes?.size)
                            dataWrapper.data = meetingAndNotesModel
                        }
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }

    fun getPlacesByUserIdAndCreationDate(date: String): LiveData<DataWrapper<PlacesByUserIdAndCreationDateResponse>> {
        val liveData: MutableLiveData<DataWrapper<PlacesByUserIdAndCreationDateResponse>> = MutableLiveData()
        val dataWrapper: DataWrapper<PlacesByUserIdAndCreationDateResponse> = DataWrapper()

        mapApi?.getPlacesByUserIdAndCreationDate(
            BuildConfig.APP_TOKEN,
            PlacesByUserIdAndCreationDate(Dynamic.uuid!!, date)
        )?.enqueue(object : Callback<PlacesByUserIdAndCreationDateResponse> {
            override fun onFailure(call: Call<PlacesByUserIdAndCreationDateResponse>, t: Throwable) {
                dataWrapper.throwable = t
                liveData.value = dataWrapper
            }

            override fun onResponse(
                call: Call<PlacesByUserIdAndCreationDateResponse>,
                response: Response<PlacesByUserIdAndCreationDateResponse>
            ) {
                if (response.isSuccessful) {
                    dataWrapper.data = response.body()
                } else {
                    dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                }
                liveData.value = dataWrapper
            }

        })

        return liveData
    }

    fun getMeetingByMeetingId(meetingId: String): LiveData<DataWrapper<MeetingReportModel>> {
        val liveData: MutableLiveData<DataWrapper<MeetingReportModel>> = MutableLiveData()
        val dataWrapper: DataWrapper<MeetingReportModel> = DataWrapper()

        mapApi?.getMeetingsByMeetingId(BuildConfig.APP_TOKEN, GetByMeetingId(Dynamic.uuid!!, meetingId))
            ?.enqueue(object : Callback<MeetingReportResponse> {
                override fun onFailure(call: Call<MeetingReportResponse>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(call: Call<MeetingReportResponse>, response: Response<MeetingReportResponse>) {
                    if (response.isSuccessful) {
                        if (response.body()?.error == false) {
                            dataWrapper.data = response.body()?.meetingReportModel
                        } else {
                            dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                        }
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }
}