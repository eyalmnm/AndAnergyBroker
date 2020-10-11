package com.usenergysolutions.energybroker.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PhotoModel(
    private var photoWidth: Int? = null,
    private var photoHeight: Int? = null,
    private var photo_reference: String? = null
) : Parcelable {

    fun getWidth(): Int? {
        return photoWidth
    }

    fun getHeight(): Int? {
        return photoHeight
    }

    fun getPhotoReference(): String? {
        return photo_reference
    }
}