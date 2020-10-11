package com.usenergysolutions.energybroker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DailyReportModel(
    @SerializedName("date")
    @Expose
    val date: String,

    @SerializedName("places")
    @Expose
    val places: MutableList<UesPlaceModel>?,

    @SerializedName("meetings")
    @Expose
    val meetings: MutableList<MeetingReportModel>?
)