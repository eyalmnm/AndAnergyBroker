package com.usenergysolutions.energybroker.model.outgoing.map

class UpdatePlaceModel(
    var uuid: String,
    var placeId: String,
    var name: String?,
    var address: String?,
    var phone: String?,
    var email: String?,
    var type: Int,
    var updated_at: String
)