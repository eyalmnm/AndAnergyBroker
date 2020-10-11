package com.usenergysolutions.energybroker.model.outgoing.map

class StoreMeeting(
    var uuid: String,
    var placeId: String,
    var businessName: String,
    var businessAddress: String,
    var meetingStatus: Int,
    var note: String?,
    var reminder: String,
    var contacts: String,
    var seeTheBill: Int?,
    var time: String
)