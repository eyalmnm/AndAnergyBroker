package com.usenergysolutions.energybroker.model.incoming.map.google

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Result(
    @SerializedName("formatted_address")
    @Expose
    var formattedAddress: String,

    @SerializedName("geometry")
    @Expose
    var geometry: Geometry,

    @SerializedName("icon")
    @Expose
    var icon: String,

    @SerializedName("international_phone_number")
    @Expose
    var internationalPhoneNumber: String,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("place_id")
    @Expose
    var placeId: String,

    @SerializedName("types")
    @Expose
    var types: MutableList<String>
)