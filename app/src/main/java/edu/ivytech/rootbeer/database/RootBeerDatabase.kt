package edu.ivytech.rootbeer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities =[RootBeer::class], version = 1)
@TypeConverters(RootBeerTypeConverters::class)
abstract class RootBeerDatabase : RoomDatabase() {
    abstract fun rootBeerDAO() : RootBeerDAO
}