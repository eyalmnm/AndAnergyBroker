package com.usenergysolutions.energybroker.model.converters

import com.usenergysolutions.energybroker.config.LocationHelper
import com.usenergysolutions.energybroker.model.BusinessInfoModel
import com.usenergysolutions.energybroker.model.ExtendedBusinessInfoModel
import com.usenergysolutions.energybroker.model.PhotoModel
import com.usenergysolutions.energybroker.model.UesPlaceModel

class ToExtendedBusinessInfoModel {
    companion object {

        fun getExtendedBusinessInfoModel(uesPlaceModel: UesPlaceModel): ExtendedBusinessInfoModel? {

            val typesArray: MutableList<String> = arrayListOf()
            val typeName: String? = LocationHelper.getTypeNameByPlaceType(uesPlaceModel.businessType!!)
            typesArray.add(typeName!!)

            return ExtendedBusinessInfoModel(
                placeIdStr = uesPlaceModel.placeId.toString(),
                businessNameStr = uesPlaceModel.businessName,
                businessAddressStr = uesPlaceModel.businessAddress,
                businessPhoneStr = uesPlaceModel.businessPhone,
                typeList = typesArray,
                iconStr = LocationHelper.getImageByPlaceType(uesPlaceModel.businessType!!).toString(),
                latitudeDbl = uesPlaceModel.latitude,
                longitudeDbl = uesPlaceModel.longitude,
                businessEmaiStrl = uesPlaceModel.businessEmail,
//                openingHoursArr = uesPlaceModel.openingHoursArray,
                contactsList = uesPlaceModel.contacts
            )

        }

        fun getExtendedBusinessInfoModel(bInfoModel: BusinessInfoModel): ExtendedBusinessInfoModel? {

            return ExtendedBusinessInfoModel(
                placeIdStr = bInfoModel.getPlaceId(),
                businessNameStr = bInfoModel.getName(),
                businessAddressStr = bInfoModel.getAddress(),
                businessPhoneStr = bInfoModel.getPhoneNumber(),
                typeList = bInfoModel.getTypes() as? MutableList<String>,
                iconStr = bInfoModel.getIcon(),
                photosList = bInfoModel.getPhtos() as? MutableList<PhotoModel>,
                openNowBln = bInfoModel.isOpenNow(),
//                openingHoursArr = bInfoModel.getWeekDays(),
                ratingFlt = bInfoModel.getRating(),
                userRatingsTotalInt = bInfoModel.getUserRatingsTotal(),
                latitudeDbl = bInfoModel.getLatitude(),
                longitudeDbl = bInfoModel.getLongitude()
            )
        }
    }
}