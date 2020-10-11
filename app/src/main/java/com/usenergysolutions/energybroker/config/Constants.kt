package com.usenergysolutions.energybroker.config

import android.os.Environment

class Constants {
    companion object {

        var BASE_PATH: String = Environment.getExternalStorageDirectory().toString() + "/energy_broker"

        // SERVER SIDE CONSTANTS
        const val BEARER: String = "bearer"
        const val ERROR: String = "error"
        const val UUID: String = "uuid"
        const val ERROR_MSG: String = "error_msg"
        const val ERROR_CODE: String = "error_code"

        // Login / Registration Constants
        const val USER: String = "user"
        const val UNIQUE_ID: String = "unique_id"
        const val NAME: String = "name"
        const val EMAIL: String = "email"
        const val CREATED_AT: String = "created_at"
        const val UPDATED_AT: String = "updated_at"
        const val OFFICE_ID: String = "officeId"
        const val ID: String = "id"
        const val PROFILE_ID = "id"

        // Place Constants
        const val PLACE: String = "place"
        const val ICON: String = "icon"
        const val ADDRESS: String = "formatted_address"
        const val TYPES: String = "types"
        const val PHONE: String = "international_phone_number"
        const val PLACE_ID: String = "place_id"
        const val STATUS = "status"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val PHOTO_REF = "photo_reference"
        const val RESULT = "result"
        const val RATING = "rating"
        const val USER_RATINGS_TOTAL = "user_ratings_total"
        const val PHOTOS = "Photo"
        const val GEOMETRY = "geometry"
        const val LOCATION = "location"
        const val LAT = "lat"
        const val LNG = "lng"
        const val USE_PLACE_ID = "id"
        const val BUSINESS_NAME = "businessName"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val BUSINESS_ADDRESS = "businessAddress"
        const val BUSINESS_EMAIL = "businessEmail"
        const val BUSINESS_PHONE = "businessPhone"
        const val BUSINESS_TYPE = "businessType"
        const val CONTACTS = "contacts"
        const val CONTACTS_DELIMITER = ";"
        const val USE_PLACE_OPENING_HOURS = "openingHours"
        const val PLACES = "places"
        const val DECISION_MAKERS = "decisionMakers"
        const val MEET_DECISION_MAKER = "meetWithDecisionMaker"
        const val IMAGE_FILE = "image"
        const val FILE_NAME = "file_name"

        // Notes Constants
        const val NOTES = "notes"
        const val NOTE_ID = "id"
        const val USER_ID = "userId"
        const val AVATAR_URL = "avatar"
        const val NOTE_TEXT = "notes"
        const val NOTE = "note"
        const val USER_UNIQUE_ID: String = "user_unique_id"
        const val DATE_TIME: String = "date_time"

        // Profile constants
        const val PROFILE: String = "profile"

        // Intent and Arguments Data name Constants
        const val NAME_LATITUDE_DATA: String = "latitudeData"
        const val NAME_LONGITUDE_DATA: String = "longitudeData"
        const val NAME_SAMPLE_TIME: String = "sampleTime"
        const val NAME_TEXT_DATA: String = "textData"

        // Location Local BroadcastReceiver's actions
        const val LOCATION_AVAILABLE: String = "com.em_projects.energy_broker.location_available"
        const val LOCATION_NOT_AVAILABLE: String = "com.em_projects.energy_broker.location_not_available"

        // Local Data Transfer
        const val APP_DATA: String = "app_data"

        // Store meeting Constants
        const val MEETING: String = "meeting"
        const val MEETING_ID: String = "id"
        const val MEETING_PLACE_ID: String = "placeId"
        const val MEETING_USER_ID: String = "userId"
        const val MEETING_STATUS_ID: String = "meetingStatus"
        const val MEETING_NOTE_ID: String = "noteId"
        const val MEETING_REMINDER: String = "reminder"
        const val MEETING_CREATED_AT: String = "created_at"
        const val MEETING_UPDATED_AT: String = "updated_at"
        const val MEETING_REASON: String = "meetingReason"
        const val REPORT: String = "report"
        const val DATE: String = "date"

        // Meeting History Constants
        const val MEETINGS: String = "meetings"
        const val MEETING_AVATAR: String = "avatar"
        const val MEETING_USER_NAME: String = "userName"
        const val MEETING_STATUS_NAME: String = "statusName"
        const val MEETING_NOTES: String = "notes"
        const val MEETINGS_COUNTER: String = "meetingsCounter"

        // Meeting Statuses
        const val MEETING_STATUS_PARAM: String = "meetingStaus"
        const val MEETING_SEE_THE_BILL: String = "seeTheBill"

        //  TODO Change to string resources
        fun getMeetingResultsStatusName(statusId: Int): String {
            when (statusId) {
                Companion.MeetingResultsStatus.CLOSED.id -> return "Closed"
                Companion.MeetingResultsStatus.NO_MEETING.id -> return "No Meeting"
                Companion.MeetingResultsStatus.SIGNED.id -> return "Signed"
                Companion.MeetingResultsStatus.COME_AGAIN.id -> return "Come Again"
                Companion.MeetingResultsStatus.UNDER_CONTRACT.id -> return "Under Contract"
                Companion.MeetingResultsStatus.NOT_INTERESTED.id -> return "Not Interested"
                Companion.MeetingResultsStatus.NO_COMMENTS.id -> return "No Comment"
                Companion.MeetingResultsStatus.OTHER.id -> return "Other"
                Companion.MeetingResultsStatus.DO_NOT_COME_AGAIN.id -> return "Don't Come Again"
                Companion.MeetingResultsStatus.FAILED.id -> return "Failed"
            }
            return "No Meeting"
        }

        // Decision maker
        const val DECISION_MAKER_ID = "id"
        const val DECISION_MAKER_NAME: String = "contactName"
        const val DECISION_MAKER_PHONE: String = "contactPhone"
        const val DECISION_MAKER_EMAIL: String = "contactEmail"
        const val DECISION_MAKER_COMMENT: String = "comment"
        const val DECISION_MAKER_IS: String = "isDecisionMaker"
        const val DECISION_MAKER_COUNTER: String = "contactsCounter"

        // Meetings and  Notes Model
        const val NOTES_COUNTER: String = "notesCounter"


        // Meeting status enum
        enum class MeetingResultsStatus(val id: Int) {
            CLOSED(1),
            NO_MEETING(2),
            SIGNED(3),
            COME_AGAIN(4),
            UNDER_CONTRACT(5),
            NOT_INTERESTED(6),
            NO_COMMENTS(7),
            OTHER(8),
            DO_NOT_COME_AGAIN(9),
            FAILED(10)
        }

        // History Item type
        enum class ItemType(val id: Int) {
            MEETING(1),
            NOTE(2)
        }

    }
}