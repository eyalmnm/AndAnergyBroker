package com.usenergysolutions.energybroker.model

class PlaceTypeModel(var name: String?, var id: Int?) {

    fun getTypeId(): Int? {
        return id
    }

    fun getTypeName(): String? {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaceTypeModel

        if (name != other.name) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (id ?: 0)
        return result
    }

    override fun toString(): String {
        return "PlaceTypeModel(name=$name, id=$id)"
    }


}