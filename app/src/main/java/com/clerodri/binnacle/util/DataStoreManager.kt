package com.clerodri.binnacle.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.clerodri.binnacle.home.presentation.HomeScreenViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


const val HOME_DATASTORE = "home_data"

val Context.dataStore  by preferencesDataStore(name = HOME_DATASTORE)

class DataStoreManager @Inject constructor(private val context: Context) {

    companion object{
        private val CURRENT_INDEX_KEY = intPreferencesKey("current_index")
        private val IS_STARTED_KEY = booleanPreferencesKey("is_started")
        private val IS_LOADING_KEY = booleanPreferencesKey("is_loading")
        private val IS_ROUND_BTN_ENABLED_KEY = booleanPreferencesKey("is_round_btn_enabled")
        private val TIMER_KEY = stringPreferencesKey("timer")
        private val ELAPSED_SECONDS_KEY = intPreferencesKey("elapsed_seconds")
        private val IS_CHECK_IN_KEY = booleanPreferencesKey("is_check_in")
        private val IS_CHECK_BTN_KEY = booleanPreferencesKey("is_check_btn_enable")
    }

    val homeScreenState: Flow<HomeScreenViewState> = context.dataStore.data.map { preferences ->
        HomeScreenViewState(
            currentIndex = preferences[CURRENT_INDEX_KEY] ?: 0,
            isStarted = preferences[IS_STARTED_KEY] ?: false,
            isLoading = preferences[IS_LOADING_KEY] ?: false,
            isRoundBtnEnabled = preferences[IS_ROUND_BTN_ENABLED_KEY] ?: true,
            timer = preferences[TIMER_KEY] ?: "00:00:00",
            elapsedSeconds = preferences[ELAPSED_SECONDS_KEY] ?: 0,
            isCheckedIn = preferences[IS_CHECK_IN_KEY] ?: false,
            enableCheck = preferences[IS_CHECK_BTN_KEY] ?: true
        )
    }


    suspend fun saveState(state: HomeScreenViewState) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_INDEX_KEY] = state.currentIndex
            preferences[IS_STARTED_KEY] = state.isStarted
            preferences[IS_LOADING_KEY] = state.isLoading
            preferences[IS_ROUND_BTN_ENABLED_KEY] = state.isRoundBtnEnabled
            preferences[TIMER_KEY] = state.timer
            preferences[ELAPSED_SECONDS_KEY] = state.elapsedSeconds
            preferences[IS_CHECK_IN_KEY] = state.isCheckedIn
            preferences[IS_CHECK_BTN_KEY] = state.enableCheck
        }
    }
}