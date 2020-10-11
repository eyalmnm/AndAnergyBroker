package com.usenergysolutions.energybroker.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class UesPlaceModel(
    @SerializedName("id")
    @Expose
    var placeId: Int? = null,

    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null,

    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null,

    @SerializedName("businessName")
    @Expose
    var businessName: String? = null,

    @SerializedName("businessAddress")
    @Expose
    var businessAddress: String? = null,

    @SerializedName("businessEmail")
    @Expose
    var businessEmail: String? = null,

    @SerializedName("businessPhone")
    @Expose
    var businessPhone: String? = null,

    @SerializedName("businessType")
    @Expose
    var businessType: Int? = null,

//    @SerializedName("openingHours")
//    @Expose
//    var openingHoursArray: Array<String>? = null,

    @SerializedName("contacts")
    @Expose
    var contacts: List<DecisionMakerModel>? = null,

    @SerializedName("created_at")
    @Expose
    var creationDate: String? = null,

    @SerializedName("updated_at")
    @Expose
    var updateDate: String? = null
) : Parcelable