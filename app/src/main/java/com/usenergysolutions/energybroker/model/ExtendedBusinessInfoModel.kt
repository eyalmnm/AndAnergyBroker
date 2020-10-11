package com.usenergysolutions.energybroker.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Ref: https://stackoverflow.com/questions/33551972/is-there-a-convenient-way-to-create-parcelable-data-classes-in-android-with-kotl

@Parcelize
class ExtendedBusinessInfoModel(
    var placeIdStr: String? = null,
    var iconStr: String? = null,
    var typeList: MutableList<String>? = null,
    var ratingFlt: Float? = null,
    var photosList: MutableList<PhotoModel>? = null,
    var userRatingsTotalInt: Int? = null,
    var openNowBln: Boolean? = null,
    var latitudeDbl: Double? = null,
    var longitudeDbl: Double? = null,
    var businessNameStr: String? = null,
    var businessAddressStr: String? = null,
    var businessEmaiStrl: String? = null,
    var businessPhoneStr: String? = null,
//    var openingHoursArr: Array<String>? = null,
    var contactsList: List<DecisionMakerModel>? = null
) : Parcelable {


    fun getPlaceId(): String? {
        return placeIdStr
    }

    fun getIcon(): String? {
        return iconStr
    }

    fun getType(): List<String>? {
        return typeList
    }

    fun getRating(): Float? {
        return ratingFlt
    }

    fun getPhotos(): MutableList<PhotoModel>? {
        return photosList
    }

    fun getUserRatingsTotal(): Int? {
        return userRatingsTotalInt
    }

    fun isOpenNow(): Boolean? {
        return openNowBln
    }

    fun getLatitude(): Double? {
        return latitudeDbl
    }

    fun getLongitude(): Double? {
        return longitudeDbl
    }

    fun getBusinessName(): String? {
        return businessNameStr
    }

    fun getBusinessAddress(): String? {
        return businessAddressStr
    }

    fun getBusinessPhone(): String? {
        return businessPhoneStr
    }

    fun getBusinessEmail(): String? {
        return businessEmaiStrl
    }

//    fun getOpeningHoursArray(): Array<String>? {
//        return openingHoursArr
//    }

    fun getContacts(): List<DecisionMakerModel>? {
        return contactsList
    }
}