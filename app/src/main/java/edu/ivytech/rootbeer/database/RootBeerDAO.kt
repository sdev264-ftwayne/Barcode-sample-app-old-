package edu.ivytech.rootbeer.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

@Dao
interface RootBeerDAO {
    @Query("SELECT * FROM RootBeer")
    fun getAllRootBeer(): LiveData<List<RootBeer>>


    @Query("SELECT * FROM RootBeer WHERE id = (:rootBeerID)")
    fun getRootBeer(rootBeerID: UUID) : LiveData<RootBeer>


    @Insert
    fun addRB(rb: RootBeer)

    @Update
    fun updateRB(rb: RootBeer)

}