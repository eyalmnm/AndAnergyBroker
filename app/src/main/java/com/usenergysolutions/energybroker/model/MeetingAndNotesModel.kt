package com.usenergysolutions.energybroker.model

class MeetingAndNotesModel {

    var meetingsData: MutableList<MeetingModel>? = null
    var notesData: MutableList<NoteModel>? = null

    var meetingsCounter: Int? = null
    var notesCounter: Int? = null

    fun getMeetings(): MutableList<MeetingModel>? {
        return meetingsData
    }

    fun setMeeting(meetingsData: MutableList<MeetingModel>?) {
        this.meetingsData = meetingsData
    }

    fun getNotes(): MutableList<NoteModel>? {
        return notesData
    }

    fun setNotes(notesData: MutableList<NoteModel>?) {
        this.notesData = notesData
    }

    fun getMeetingCounter(): Int? {
        return meetingsCounter
    }

    fun setMeetingCounter(meetingsCounter: Int?) {
        this.meetingsCounter = meetingsCounter
    }

    fun getNoteCounter(): Int? {
        return notesCounter
    }

    fun setNoteCounter(notesCounter: Int?) {
        this.notesCounter = notesCounter
    }
}