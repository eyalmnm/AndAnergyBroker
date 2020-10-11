package com.usenergysolutions.energybroker.model.outgoing.map

class AddBusinessModel(
    var uuid: String, var latitude: Double, var longitude: Double, var name: String,
    var address: String?, var email: String?, var phone: String?, var type: Int,
    var contacts: String?, var openingHours: String?, var time: String
)