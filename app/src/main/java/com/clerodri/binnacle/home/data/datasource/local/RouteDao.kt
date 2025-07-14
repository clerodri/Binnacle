package com.clerodri.binnacle.home.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Author: Ronaldo R.
 * Date:  7/12/2025
 * Description:
 **/
@Dao
interface RouteDao {

    @Query("SELECT * FROM routes")
    suspend fun getAll(): List<RouteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routes: List<RouteEntity>)
}