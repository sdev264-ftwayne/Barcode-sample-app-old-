package edu.ivytech.rootbeer.database

import androidx.room.TypeConverter
import java.util.*

class RootBeerTypeConverters {

    @TypeConverter
    fun fromUUID(id : UUID) : String {
        return id.toString()
    }

    @TypeConverter
    fun toUUID (id : String?) : UUID? {
        return UUID.fromString(id)
    }
}