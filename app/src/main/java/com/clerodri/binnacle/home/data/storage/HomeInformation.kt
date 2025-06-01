package com.clerodri.binnacle.home.data.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.clerodri.binnacle.home.domain.model.Home
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


const val HOME_DATASTORE = "home_data"

val Context.homeStore by preferencesDataStore(name = HOME_DATASTORE)

class HomeInformation @Inject constructor(private val context: Context) {

    companion object {
        private val CURRENT_INDEX_KEY = intPreferencesKey("current_index")
        private val IS_STARTED_KEY = booleanPreferencesKey("is_started")
        private val ELAPSED_SECONDS_KEY = intPreferencesKey("elapsed_seconds")
        private val ROUND_ID = longPreferencesKey("round_id")
        private val CHECK_IN_ID = intPreferencesKey("check_id")
    }

    val homeData: Flow<Home> = context.homeStore.data.map { preferences ->
        Home(
            currentIndex = preferences[CURRENT_INDEX_KEY] ?: 0,
            isStarted = preferences[IS_STARTED_KEY] ?: false,
            elapsedSeconds = preferences[ELAPSED_SECONDS_KEY]?.toLong() ?: 0,
            roundId = preferences[ROUND_ID] ?: 0,
            checkId = preferences[CHECK_IN_ID] ?: 0
        )
    }


    suspend fun saveHomeState(home: Home) {
        context.homeStore.edit { preferences ->
            preferences[CURRENT_INDEX_KEY] = home.currentIndex
            preferences[IS_STARTED_KEY] = home.isStarted
            preferences[ELAPSED_SECONDS_KEY] = home.elapsedSeconds.toInt()
            preferences[ROUND_ID] = home.roundId
            preferences[CHECK_IN_ID] = home.checkId
        }
    }

    suspend fun clearHomeState() {

        context.homeStore.edit {
            Log.d("clearHomeState", "MESSAGE CLEARON")
            it.clear()
        }
    }
}