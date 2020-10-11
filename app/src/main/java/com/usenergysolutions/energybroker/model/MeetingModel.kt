package com.usenergysolutions.energybroker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.utils.CalendarUtils
import com.usenergysolutions.energybroker.utils.StringUtils

class MeetingModel : BusinessHistoryItem {

    @SerializedName("id")
    @Expose
    private var id: String? = null

    @SerializedName("reminder")
    @Expose
    private var reminder: String? = null

    @SerializedName("created_at")
    @Expose
    private var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    private var updatedAt: String? = null

    @SerializedName("avatar")
    @Expose
    private var avatar: String? = null

    @SerializedName("userName")
    @Expose
    private var userName: String? = null

    @SerializedName("statusName")
    @Expose
    private var statusName: String? = null

    @SerializedName("notes")
    @Expose
    private var notes: String? = null


    fun setId(id: String?) {
        this.id = id
    }

    fun getReminder(): String? {
        return reminder
    }

    fun getReminderMillis(): Long? {
        return CalendarUtils.getDateTimeMillis(this.reminder)
    }

    fun setReminder(reminder: String?) {
        this.reminder = reminder
    }

    fun getCreatedAt12H(): String? {
        if (createdAt != null) {
            return CalendarUtils.getCurrentLocalTime12HString(getCreatedAtMillis()!!)
        } else {
            return null
        }
    }

    fun getCreatedAt24H(): String? {
        return createdAt
    }

    fun getCreatedAtMillis(): Long? {
        return CalendarUtils.getDateTimeMillis(this.createdAt)
    }

    fun setCreatedAt(createdAt: String?) {
        this.createdAt = createdAt
    }

    fun getUpdatedAt12H(): String? {
        if (this.updatedAt != null) {
            return CalendarUtils.getCurrentLocalTime12HString(getUpdatedAtMillis()!!)
        } else {
            return null
        }
    }

    fun getUpdatedAt24H(): String? {
        return updatedAt
    }

    fun getUpdatedAtMillis(): Long? {
        return CalendarUtils.getDateTimeMillis(this.updatedAt)
    }

    fun setUpdateAt(updatedAt: String?) {
        if (updatedAt == null || updatedAt == "null" || updatedAt.startsWith("0000")) {
            this.updatedAt = null
        } else {
            this.updatedAt = updatedAt
        }
    }

    fun getAvatar(): String? {
        return avatar
    }

    fun setAvatar(avatar: String?) {
        this.avatar = avatar
    }

    fun setUserName(userName: String?) {
        this.userName = userName
    }

    fun getStatusName(): String? {
        return statusName
    }

    fun setStatusName(statusName: String?) {
        this.statusName = statusName
    }

    fun getNotes(): String? {
        if (notes?.contains("null", true) == true) {
            return null
        }
        return notes
    }

    fun setNotes(notes: String?) {
        if (notes?.contains("null", true) == true) {
            this.notes = null
        } else {
            this.notes = notes
        }
    }

    fun getFormattedStatus(): String? {
        return StringUtils.placeTypeAndTimeCleaner(statusName)
    }

    override fun getMostUpdatedDate(): Long? {
        if (updatedAt.isNullOrEmpty() || updatedAt?.startsWith("0000")!!) {
            return getCreatedAtMillis()
        } else {
            return getUpdatedAtMillis()
        }
    }


    // BusinessHistoryItem Interface Methods
    override fun getType(): Constants.Companion.ItemType {
        return Constants.Companion.ItemType.MEETING
    }

    override fun getAvatarUrl(): String? {
        return avatar
    }

    override fun getStatus(): String? {
        return getFormattedStatus()
    }

    override fun getRemainder(): String? {
        return reminder
    }

    override fun getNote(): String? {
        return notes
    }

    override fun getUserName(): String? {
        return userName
    }

    override fun getId(): String? {
        return id
    }

}