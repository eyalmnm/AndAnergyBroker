package com.usenergysolutions.energybroker.config

class OpeningHoursPatch {
    companion object {

        fun getOpeningHours(): MutableList<String> {
            val retArray: MutableList<String> = arrayListOf()
            retArray.add("Monday: 00:00 AM  00:00 PM")
            retArray.add("Tuesday: 00:00 AM  00:00 PM")
            retArray.add("Wednesday: 00:00 AM  00:00 PM")
            retArray.add("Thursday: 00:00 AM  00:00 PM")
            retArray.add("Friday: 00:00 AM  00:00 PM")
            retArray.add("Saturday: 00:00 AM  00:00 PM")
            retArray.add("Sunday: 00:00 AM  00:00 PM")
            return retArray
        }
    }
}