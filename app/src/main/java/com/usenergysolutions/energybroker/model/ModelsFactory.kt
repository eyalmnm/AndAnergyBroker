package com.usenergysolutions.energybroker.model

import com.usenergysolutions.energybroker.model.incoming.map.google.PlaceData

class ModelsFactory {
    companion object {
        private const val TAG: String = "ModelsFactory"

        fun createBusinessInfoModel(placeData: PlaceData?): BusinessInfoModel? {

            if (placeData != null) {
                val bIM = BusinessInfoModel()
                bIM.setPlaceId(placeData.result.placeId)
                bIM.setName(placeData.result.name)
                bIM.setIcon(placeData.result.icon)
                bIM.setPhoneNumber(placeData.result.internationalPhoneNumber)
                bIM.setAddress(placeData.result.formattedAddress)
                bIM.setTypes(placeData.result.types)
                bIM.setRating(5F)
                bIM.setPhotos(null)
                bIM.setUserRatingsTotal(1)
                bIM.setLatitude(placeData.result.geometry.location.lat)
                bIM.setLongitude(placeData.result.geometry.location.lng)
                return bIM
            }
            return null
        }
    }
}