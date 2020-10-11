package com.usenergysolutions.energybroker.config

class Errors {
    companion object {

        // User 1000 - 1099
        const val USER_LOGIN_FAILED = 1000
        const val USER_NOT_LOGGED_IN = 1001
        const val USER_MISSING_PARAMS = 1002
        const val USER_UNKNOWN_USER = 1003
        const val USER_ALREADY_EXIST = 1004
        const val USER_FAILED_TO_REGISTER = 1005
        const val USER_FAILED_TO_UPDATE_USER = 1006
        const val USER_FAILED_TO_STORE_AVATAR = 1007


        // Place 1100 - 1199
        const val PLACE_FAILED_TO_ADD_PLACE = 1100
        const val PLACE_FAILED_TO_ADD_CONTACT = 1101
        const val PLACE_FAILED_TO_ADD_NOTE = 1102
        const val PLACE_FAILED_TO_ADD_MEETING = 1103
        const val PLACE_FAILED_TO_GET_NEAR_PLACES = 1104
        const val PLACE_FAILED_TO_GET_NOTES = 1105
        const val PLACE_FAILED_TO_GET_PLACES = 1106
        const val PLACE_MISSING_PARAMS = 1007
        const val PLACE_FAILED_TO_PARSE_CONTACTS = 1008

    }
}