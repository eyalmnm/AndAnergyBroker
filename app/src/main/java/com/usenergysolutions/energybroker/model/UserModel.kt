package com.usenergysolutions.energybroker.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserModel(

    @SerializedName("uuid")
    @Expose
    var uuid: String? = null,

    @SerializedName("unique_id")
    @Expose
    var uniqueId: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("user")
    @Expose
    var user: String? = null,

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null,

    @SerializedName("email")
    @Expose
    var email: String? = null,

    @SerializedName("created_at")
    @Expose
    var created_at: String? = null,

    @SerializedName("updated_at")
    @Expose
    var updated_at: String? = null,

    @SerializedName("officeId")
    @Expose
    var officeId: Int? = null,

    @SerializedName("id")
    @Expose
    var id: Int? = null
) : Parcelable