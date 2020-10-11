package com.usenergysolutions.energybroker.model

class DayOpeningModel {

    var name: String? = null
    var isClosed: Boolean? = null
    var frmHrs: String? = null
    var frmMin: String? = null
    var frmTypeStr: String? = null
    var tilHrs: String? = null
    var tilMin: String? = null
    var tilTypeStr: String? = null

    fun isEmpty(): Boolean {
        return name.isNullOrEmpty() && frmHrs.isNullOrEmpty() && frmMin.isNullOrEmpty()
                && tilHrs.isNullOrEmpty() && tilMin.isNullOrEmpty()
    }
}