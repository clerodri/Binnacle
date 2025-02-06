package com.clerodri.binnacle.home.data.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        private val IS_ROUND_BTN_ENABLED_KEY = booleanPreferencesKey("is_round_btn_enabled")
        private val TIMER_KEY = stringPreferencesKey("timer")
        private val ELAPSED_SECONDS_KEY = intPreferencesKey("elapsed_seconds")
        private val IS_CHECK_IN_KEY = booleanPreferencesKey("is_check_in")
        private val IS_CHECK_OUT_KEY = booleanPreferencesKey("is_check_out")
        private val IS_CHECK_BTN_KEY = booleanPreferencesKey("is_check_btn_enable")
    }

    val homeData: Flow<Home> = context.homeStore.data.map { preferences ->
        Home(
            currentIndex = preferences[CURRENT_INDEX_KEY] ?: 0,
            isStarted = preferences[IS_STARTED_KEY] ?: false,
            isRoundBtnEnabled = preferences[IS_ROUND_BTN_ENABLED_KEY] ?: true,
            timer = preferences[TIMER_KEY] ?: "00:00:00",
            elapsedSeconds = preferences[ELAPSED_SECONDS_KEY] ?: 0,
            isCheckedIn = preferences[IS_CHECK_IN_KEY] ?: false,
            isCheckedOut = preferences[IS_CHECK_OUT_KEY] ?: false,
            enableCheck = preferences[IS_CHECK_BTN_KEY] ?: true
        )
    }


    suspend fun saveHomeState(home: Home) {
        context.homeStore.edit { preferences ->
            preferences[CURRENT_INDEX_KEY] = home.currentIndex
            preferences[IS_STARTED_KEY] = home.isStarted
            preferences[IS_ROUND_BTN_ENABLED_KEY] = home.isRoundBtnEnabled
            preferences[TIMER_KEY] = home.timer
            preferences[ELAPSED_SECONDS_KEY] = home.elapsedSeconds
            preferences[IS_CHECK_IN_KEY] = home.isCheckedIn
            preferences[IS_CHECK_OUT_KEY] = home.isCheckedOut
            preferences[IS_CHECK_BTN_KEY] = home.enableCheck
        }
    }

    suspend fun clearHomeState() {
        context.homeStore.edit {
            it.clear()
        }
    }
}