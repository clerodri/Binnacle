package com.clerodri.binnacle.home.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Author: Ronaldo R.
 * Date:  7/12/2025
 * Description:
 **/
@Database(entities = [RouteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun routeDao(): RouteDao

}
