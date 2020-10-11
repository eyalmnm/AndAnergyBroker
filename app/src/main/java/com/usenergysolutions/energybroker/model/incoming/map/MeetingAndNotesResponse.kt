package com.usenergysolutions.energybroker.model.incoming.map

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.usenergysolutions.energybroker.model.MeetingModel
import com.usenergysolutions.energybroker.model.NoteModel

class MeetingAndNotesResponse(
    @SerializedName("error")
    @Expose
    var error: Boolean,

    @SerializedName("error_msg")
    @Expose
    var errorMsg: String? = null,

    @SerializedName("error_code")
    @Expose
    var errorCode: Int? = null,

    @SerializedName("meetings")
    @Expose
    var meetings: MutableList<MeetingModel>?,

    @SerializedName("notes")
    @Expose
    var notes: MutableList<NoteModel>?
)