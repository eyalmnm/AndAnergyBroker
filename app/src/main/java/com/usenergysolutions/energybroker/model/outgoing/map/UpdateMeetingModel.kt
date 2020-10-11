package com.usenergysolutions.energybroker.model.outgoing.map

class UpdateMeetingModel(
    var uuid: String, var meetingId: Int, var placeId: String, var businessName: String, var businessAddress: String?,
    var noteId: Int?, var note: String?, var reminder: String?, var meetingStatus: Int, var contacts: String?,
    var seeTheBill: Int?, var time: String
)