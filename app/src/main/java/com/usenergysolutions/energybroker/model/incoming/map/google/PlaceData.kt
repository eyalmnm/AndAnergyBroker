package com.usenergysolutions.energybroker.model.incoming.map.google

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class PlaceData(
    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: MutableList<Objects>,

    @SerializedName("result")
    @Expose
    var result: Result,

    @SerializedName("status")
    @Expose
    var status: String
)