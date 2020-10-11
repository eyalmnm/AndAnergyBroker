package com.usenergysolutions.energybroker.model

import android.net.Uri
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import java.util.*

class PlaceModel(place: Place) {

    // Properties
    private var address: String? = place.address?.toString()
    private var attributions: String? = place.attributions?.toString()
    private var id: String? = place.id
    private var latLng: LatLng? = place.latLng
    private var locale: Locale? = place.locale
    private var name: String? = place.name?.toString()
    private var phoneNumber: String? = place.phoneNumber!!.toString()
    private var placeTypes: MutableList<Int>? = place.placeTypes
    private var priceLevel: Int = place.priceLevel
    private var rating: Float = place.rating
    private var viewport: LatLngBounds? = place.viewport
    private var websiteUri: Uri? = place.websiteUri

    fun getAddress(): String? {
        return address
    }

    fun getAttributions(): String? {
        return attributions
    }

    fun getId(): String? {
        return id
    }

    fun getLatLng(): LatLng? {
        return latLng
    }

    fun getLocale(): Locale? {
        return locale
    }

    fun getName(): String? {
        return name
    }

    fun getPhoneNumber(): String? {
        return phoneNumber
    }

    fun getPlaceTypes(): MutableList<Int>? {
        return placeTypes
    }

    fun getPriceLevel(): Int {
        return priceLevel
    }

    fun getRating(): Float {
        return rating
    }

    fun getViewport(): LatLngBounds? {
        return viewport
    }

    fun getWebsiteUri(): Uri? {
        return websiteUri
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaceModel

        if (address != other.address) return false
        if (attributions != other.attributions) return false
        if (id != other.id) return false
        if (latLng != other.latLng) return false
        if (locale != other.locale) return false
        if (name != other.name) return false
        if (phoneNumber != other.phoneNumber) return false
        if (placeTypes != other.placeTypes) return false
        if (priceLevel != other.priceLevel) return false
        if (rating != other.rating) return false
        if (viewport != other.viewport) return false
        if (websiteUri != other.websiteUri) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address?.hashCode() ?: 0
        result = 31 * result + (attributions?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (latLng?.hashCode() ?: 0)
        result = 31 * result + (locale?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (phoneNumber?.hashCode() ?: 0)
        result = 31 * result + (placeTypes?.hashCode() ?: 0)
        result = 31 * result + priceLevel
        result = 31 * result + rating.hashCode()
        result = 31 * result + (viewport?.hashCode() ?: 0)
        result = 31 * result + (websiteUri?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "PlaceModel(address=$address, attributions=$attributions, id=$id, latLng=$latLng, locale=$locale, name=$name, phoneNumber=$phoneNumber, placeTypes=$placeTypes, priceLevel=$priceLevel, ratingFlt=$rating, viewport=$viewport, websiteUri=$websiteUri)"
    }


}