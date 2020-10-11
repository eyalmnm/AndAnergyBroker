package com.usenergysolutions.energybroker.model.incoming.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.usenergysolutions.energybroker.model.UserModel

class GetProfileResponse(
    @SerializedName("error")
    @Expose
    var error: Boolean,

    @SerializedName("error_msg")
    @Expose
    var errorMsg: String? = null,

    @SerializedName("error_code")
    @Expose
    var errorCode: Int? = null,

    @SerializedName("profile")
    @Expose
    var profile: UserModel
)