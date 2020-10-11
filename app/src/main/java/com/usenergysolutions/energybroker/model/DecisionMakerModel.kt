package com.usenergysolutions.energybroker.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

// Ref: https://stackoverflow.com/questions/33551972/is-there-a-convenient-way-to-create-parcelable-data-classes-in-android-with-kotl

@Parcelize
open class DecisionMakerModel(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("contactName")
    @Expose
    var name: String? = null,

    @SerializedName("contactEmail")
    @Expose
    var email: String? = null,

    @SerializedName("contactPhone")
    @Expose
    var phone: String? = null,

    @SerializedName("comment")
    @Expose
    var comment: String? = null,

    @SerializedName("isDecisionMaker")
    @Expose
    var isDecisionMaker: Int?
) : Parcelable {

    fun isEmpty(): Boolean {
        return name.isNullOrEmpty() && email.isNullOrEmpty() && phone.isNullOrEmpty()
    }

    override fun toString(): String {
        return "DecisionMakerModel(id=$id, name=$name, email=$email, phone=$phone, comment=$comment, isDecisionMaker=$isDecisionMaker)"
    }

    open fun getAsString(): String {
        return "id=$id, name=$name, email=$email, phone=$phone, comment=$comment, isDecisionMaker=$isDecisionMaker"
    }
}