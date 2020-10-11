package com.usenergysolutions.energybroker.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
open class MeetingReposeModel(

    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("place_id")
    @Expose
    var placeId: String? = null,

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null,

    @SerializedName("meeting_status_id")
    @Expose
    var meetingStatusId: Int? = null,

    @SerializedName("note_id")
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
    var seeTheBill: Int? = null

) : Parcelable