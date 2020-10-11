package com.usenergysolutions.energybroker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.utils.CalendarUtils

class NoteModel : BusinessHistoryItem {
    @SerializedName("id")
    @Expose
    private var id: String? = null

    @SerializedName("place_id")
    @Expose
    private var placeId: String? = null

    @SerializedName("avatar")
    @Expose
    private var avatarUrl: String? = null

    @SerializedName("name")
    @Expose
    private var creatorName: String? = null

    @SerializedName("notes")
    @Expose
    private var noteText: String? = null

    @SerializedName("date_time")
    @Expose
    private var dateTime: String? = null

    @SerializedName("updated_at")
    @Expose
    private var updatedAt: String? = null

    @SerializedName("userId")
    @Expose
    private var userId: String? = null


    fun setId(id: String?) {
        this.id = id
    }

    fun getPlaceId(): String? {
        return placeId
    }

    fun setPlaceId(placeId: String?) {
        this.placeId = placeId
    }

    fun setAvatarUrl(avatarUrl: String?) {
        this.avatarUrl = avatarUrl
    }

    fun getCreatorName(): String? {
        return creatorName
    }

    fun setCreatorName(creatorName: String?) {
        this.creatorName = creatorName
    }

    fun getNoteText(): String? {
        if (noteText?.contains("null", true) == true) {
            return null
        }
        return noteText
    }

    fun setNoteText(noteText: String?) {
        if (noteText?.contains("null", true) == true) {
            this.noteText = null
        } else {
            this.noteText = noteText
        }
    }

    fun getDateTime12H(): String? {
        if (dateTime != null) {
            return CalendarUtils.getCurrentLocalTime12HString(getDateTimeMillis()!!)
        } else {
            return null
        }
    }

    fun getDateTime24H(): String? {
        return dateTime
    }

    fun setDateTime(dateTime: String?) {
        this.dateTime = dateTime
    }

    fun getDateTimeMillis(): Long? {
        return CalendarUtils.getDateTimeMillis(dateTime)
    }

    fun getUpdatedAt12H(): String? {
        if (updatedAt != null) {
            return CalendarUtils.getCurrentLocalTime12HString(getUpdatedAtMillis()!!)
        } else {
            return null
        }
    }

    fun getUpdatedAt24H(): String? {
        return updatedAt
    }

    fun setUpdatedAt(updatedAt: String?) {
        if (updatedAt == null || updatedAt == "null" || updatedAt.startsWith("0000")) {
            this.updatedAt = null
        } else {
            this.updatedAt = updatedAt
        }
    }

    fun getUpdatedAtMillis(): Long? {
        return CalendarUtils.getDateTimeMillis(updatedAt)
    }

    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String?) {
        this.userId = userId
    }

    // Sample incoming date 2019-01-28 12:39:59
//    fun getDateTimeMillis(dateTimeStr: String?): Long? {
//        var spf = SimpleDateFormat("yyy-mm-dd HH:mm:ss")
//        val newDate = spf.parse(dateTimeStr)
//        return newDate?.time
//    }


    // BusinessHistoryItem Interface Methods
    override fun getMostUpdatedDate(): Long? {
        if (updatedAt.isNullOrEmpty() || updatedAt?.startsWith("0000")!!) {
            return getDateTimeMillis()
        } else {
            return getUpdatedAtMillis()
        }
    }

    override fun getType(): Constants.Companion.ItemType {
        return Constants.Companion.ItemType.NOTE
    }

    override fun getId(): String? {
        return id
    }

    override fun getAvatarUrl(): String? {
        return avatarUrl
    }

    override fun getUserName(): String? {
        return creatorName
    }

    override fun getStatus(): String? {
        return null
    }

    override fun getRemainder(): String? {
        return null
    }

    override fun getNote(): String? {
        return noteText
    }


}