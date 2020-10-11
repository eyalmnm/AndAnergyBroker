package com.usenergysolutions.energybroker.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class MeetingReportModel(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("placeId")
    @Expose
    var placeId: String? = null,

    @SerializedName("userId")
    @Expose
    var userId: Int? = null,

    @SerializedName("meetingStatus")
    @Expose
    var meetingStatusId: Int? = null,

    @SerializedName("noteId")
    @Expose
    var note_id: Int? = null,

    @SerializedName("reminder")
    @Expose
    var reminder: String? = null,

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,

    @SerializedName("seeTheBill")
    @Expose
    var seeTheBill: Int? = null,

    @SerializedName("place")
    @Expose
    var place: UesPlaceModel? = null,

    @SerializedName("contacts")
    @Expose
    var decisionMakers: MutableList<DecisionMakerModel>? = null,

    @SerializedName("statusName")
    @Expose
    var meetingStatus: String? = null,

    @SerializedName("note")
    @Expose
    var note: String? = null,

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

) : Parcelable {

    fun setDecisionMakersList(decisionMakers: List<DecisionMakerModel>?) {
        this.decisionMakers = decisionMakers as? MutableList<DecisionMakerModel>
    }

    fun getDecisionMakersList(): List<DecisionMakerModel>? {
        return decisionMakers
    }

    fun getMeetingPlace(): UesPlaceModel? {
        return place
    }

    fun setMeetingPlace(place: UesPlaceModel?) {
        this.place = place
    }

    fun getBusinessName(): String? {
        return place?.businessName
    }

    fun getBusinessAddress(): String? {
        return place?.businessAddress
    }
}