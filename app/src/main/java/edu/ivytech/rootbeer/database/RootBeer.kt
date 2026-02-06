package edu.ivytech.rootbeer.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class RootBeer(@PrimaryKey val id: UUID = UUID.randomUUID(),
                    var name : String = "",
                    var manufacturer : String = "",
                    var rating: Float = 0.0f,
                    var flavor : String = "",
                    var location : String = "",
                    var quantity : Int = 0,
                    var barcode : String = ""
    ) {
    val photoFileName
        get() = "IMG_$id.jpg"
}