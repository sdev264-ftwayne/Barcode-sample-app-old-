package edu.ivytech.rootbeer

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import edu.ivytech.rootbeer.database.RootBeer
import edu.ivytech.rootbeer.database.RootBeerDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "RB_database.db"

class RootBeerRepository private constructor(context: Context){
    companion object {
        private var INSTANCE: RootBeerRepository? = null
        fun initialize(context: Context){
            INSTANCE = RootBeerRepository(context)
        }
        fun get() : RootBeerRepository {
            return INSTANCE?: throw IllegalStateException("Root Beer repository not initialized ")
        }
    }

    private val database : RootBeerDatabase = Room.databaseBuilder(
        context,
        RootBeerDatabase::class.java,
        DATABASE_NAME).build()
    private val rootBeerDAO = database.rootBeerDAO()
    private val executor = Executors.newSingleThreadExecutor()
    fun getAllRootBeer() : LiveData<List<RootBeer>> = rootBeerDAO.getAllRootBeer()
    fun getRootBeer(id: UUID) : LiveData<RootBeer> = rootBeerDAO.getRootBeer(id)
    fun addRootBeer(rootBeer: RootBeer) {
       executor.execute{ rootBeerDAO.addRB(rootBeer)}}
    fun updateRootBeer(rootBeer: RootBeer) {
        executor.execute{ rootBeerDAO.updateRB(rootBeer)}}

}