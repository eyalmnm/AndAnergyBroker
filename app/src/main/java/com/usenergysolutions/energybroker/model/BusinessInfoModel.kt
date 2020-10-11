package com.usenergysolutions.energybroker.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class BusinessInfoModel : Parcelable {

    private var placeId: String? = null
    private var name: String? = null
    private var icon: String? = null
    private var internationalPhoneNumber: String? = null
    private var formattedAddress: String? = null
    private var type: MutableList<String>? = null
    private var rating: Float? = null
    //private var opening_hours: String? = null
    private var photos: MutableList<PhotoModel>? = null
    private var userRatingsTotal: Int? = null
    private var openNow: Boolean? = null
    private var weekdayText: Array<String>? = null
    private var latitude: Double? = null
    private var longitude: Double? = null


    fun getPlaceId(): String? {
        return placeId
    }

    fun setPlaceId(placeId: String?): Unit {
        this.placeId = placeId
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?): Unit {
        this.name = name
    }

    fun getIcon(): String? {
        return icon
    }

    fun setIcon(icon: String?): Unit {
        this.icon = icon
    }

    fun getPhoneNumber(): String? {
        return internationalPhoneNumber
    }

    fun setPhoneNumber(phoneNumber: String?): Unit {
        this.internationalPhoneNumber = phoneNumber
    }

    fun getAddress(): String? {
        return formattedAddress
    }

    fun setAddress(address: String?): Unit {
        this.formattedAddress = address
    }

    fun getTypes(): List<String>? {
        return type
    }

    fun setTypes(types: MutableList<String>?): Unit {
        this.type = types
    }

    fun getRating(): Float? {
        return rating
    }

    fun setRating(rating: Float?): Unit {
        this.rating = rating
    }

    fun getPhtos(): List<PhotoModel>? {
        return photos
    }

    fun setPhotos(photos: MutableList<PhotoModel>?): Unit {
        this.photos = photos
    }

    fun getUserRatingsTotal(): Int? {
        return userRatingsTotal
    }

    fun setUserRatingsTotal(userRatingsTotal: Int?): Unit {
        this.userRatingsTotal = userRatingsTotal
    }

    fun isOpenNow(): Boolean? {
        return openNow
    }

    fun setOpenNow(openNow: Boolean?): Unit {
        this.openNow = openNow
    }

    fun getWeekDays(): Array<String>? {
        return weekdayText
    }

    fun setWeekDays(weekDays: Array<String>?): Unit {
        this.weekdayText = weekDays
    }

    fun getLatitude(): Double? {
        return latitude
    }

    fun setLatitude(latitude: Double?) {
        this.latitude = latitude
    }

    fun getLongitude(): Double? {
        return longitude
    }

    fun setLongitude(longitude: Double?) {
        this.longitude = longitude
    }

}