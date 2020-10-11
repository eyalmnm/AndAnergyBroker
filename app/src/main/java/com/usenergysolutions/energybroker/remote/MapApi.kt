package com.usenergysolutions.energybroker.remote

import com.usenergysolutions.energybroker.model.incoming.map.*
import com.usenergysolutions.energybroker.model.incoming.map.google.PlaceData
import com.usenergysolutions.energybroker.model.outgoing.map.*
import retrofit2.Call
import retrofit2.http.*

interface MapApi {

    @POST("getNotesByPlaceId.php")
    fun getNotesByPlaceId(@Header("bearer") bearer: String, @Body placeDataById: PlaceDataById): Call<GetNotesByPlaceResponse>

    @POST("addNote.php")
    fun addNote(@Header("bearer") bearer: String, @Body addNote: AddNote): Call<AddNoteResponse>

    @POST("addBusiness.php")
    fun addBusiness(@Header("bearer") bearer: String, @Body businessModel: AddBusinessModel): Call<UpdatePlaceResponse>

    @POST("updatePlace.php")
    fun updatePlace(@Header("bearer") bearer: String, @Body updatePlaceModel: UpdatePlaceModel): Call<UpdatePlaceResponse>

    @POST("getNearByPlaces.php")
    fun getNearByPlaces(@Header("bearer") bearer: String, @Body getNearByPlaces: GetNearByPlaces): Call<NearByPlacesResponse>

    @POST("getPlaceById.php")
    fun getUesPlaceDataById(@Header("bearer") bearer: String, @Body placeDataById: PlaceDataById): Call<UesPlaceDataByIdResponse>

    @GET("https://maps.googleapis.com/maps/api/place/details/json?fields=name,place_id,geometry,icon,formatted_address,type,international_phone_number")
    fun getPoiData(@Query("placeid") placeId: String, @Query("key") key: String): Call<PlaceData>

    @POST("getContactsByPlaceId.php")
    fun getContactsByPlaceId(@Header("bearer") bearer: String, @Body byPlaceId: PlaceDataById): Call<GetContactsResponse>

    @POST("storeMeetingNew.php")
    fun storeClosedNoMeetingFailed(@Header("bearer") bearer: String, @Body storeMeeting: StoreMeeting): Call<StoreMeetingResponse>

    @POST("updateMeetingNew.php")
    fun updateMeetingNew(@Header("bearer") bearer: String, @Body updateMeetingModel: UpdateMeetingModel): Call<StoreMeetingResponse>

    @POST("getMeetingsByPlaceId.php")
    fun getMeetingsByPlaceId(@Header("bearer") bearer: String, @Body byPlaceId: PlaceDataById): Call<GetMeetingsResponse>

    @POST("getMeetingsAndNotesByPlaceId.php")
    fun getMeetingsAndNotesByPlaceId(@Header("bearer") bearer: String, @Body meetingAndNotesByPlaceId: MeetingAndNotesByPlaceId): Call<MeetingAndNotesResponse>

    @POST("getPlacesByUserIdAndCreationDate.php")
    fun getPlacesByUserIdAndCreationDate(@Header("bearer") bearer: String, @Body placesByUserIdAndCreationDate: PlacesByUserIdAndCreationDate): Call<PlacesByUserIdAndCreationDateResponse>

    @POST("getMeetingsByMeetingId.php")
    fun getMeetingsByMeetingId(@Header("bearer") bearer: String, @Body getByMeetingId: GetByMeetingId): Call<MeetingReportResponse>

}