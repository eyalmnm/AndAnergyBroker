package com.usenergysolutions.energybroker.model.incoming.map

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.usenergysolutions.energybroker.model.UesPlaceModel

class NearByPlacesResponse(
    @SerializedName("error")
    @Expose
    var error: Boolean,

    @SerializedName("error_msg")
    @Expose
    var errorMsg: String? = null,

    @SerializedName("error_code")
    @Expose
    var errorCode: Int? = null,

    @SerializedName("places")
    @Expose
    var places: MutableList<UesPlaceModel>
)