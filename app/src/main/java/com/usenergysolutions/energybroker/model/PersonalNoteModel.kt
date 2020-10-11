package com.usenergysolutions.energybroker.model

class PersonalNoteModel {

    private var note: String? = null
    private var dateTime: String? = null
    private var author: String? = null
    private var id: String? = null

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getNote(): String? {
        return note
    }

    fun setNote(note: String?) {
        this.note = note
    }

    fun getDateTime(): String? {
        return dateTime
    }

    fun setDateTime(dateTime: String?) {
        this.dateTime = dateTime
    }

    fun getAuthor(): String? {
        return author
    }

    fun setAuthor(author: String?) {
        this.author = author
    }
}