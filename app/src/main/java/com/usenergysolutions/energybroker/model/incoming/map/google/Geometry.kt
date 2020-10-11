package com.usenergysolutions.energybroker.model.incoming.map.google

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Geometry(
    @SerializedName("location")
    @Expose
    var location: Location
)