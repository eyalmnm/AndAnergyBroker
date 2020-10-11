package com.usenergysolutions.energybroker.model

import com.usenergysolutions.energybroker.config.Constants

interface BusinessHistoryItem {
    fun getMostUpdatedDate(): Long?
    fun getType(): Constants.Companion.ItemType
    fun getId(): String?
    fun getAvatarUrl(): String?
    fun getUserName(): String?
    fun getStatus(): String?
    fun getRemainder(): String?
    fun getNote(): String?
}