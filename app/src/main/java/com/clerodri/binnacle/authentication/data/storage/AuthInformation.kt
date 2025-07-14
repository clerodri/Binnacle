package com.clerodri.binnacle.authentication.data.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.clerodri.binnacle.authentication.domain.model.AuthData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val AUTH_DATASTORE = "auth_data"

val Context.authDataStore by preferencesDataStore(name = AUTH_DATASTORE)

class AuthInformation @Inject constructor(

    @ApplicationContext private val context: Context
) {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        private val FULL_NAME = stringPreferencesKey("fullname")
        private val GUARD_ID = stringPreferencesKey("guardId")
        private val USER_IS_AUTHENTICATED = booleanPreferencesKey("isAuthenticated")
    }


    val authData: Flow<AuthData?> = context.authDataStore.data.map { preferences ->

        val accessToken = preferences[ACCESS_TOKEN]
        val fullName = preferences[FULL_NAME]
        val guardId = preferences[GUARD_ID]
        val isAuthenticated = preferences[USER_IS_AUTHENTICATED] ?: false
        if (accessToken != null && fullName != null && guardId != null ) {
            AuthData(accessToken, null, isAuthenticated, fullName, guardId)
        } else {
            null
        }
    }

    suspend fun saveAuthData(auth: AuthData) {
        context.authDataStore.edit { pref ->
            pref[ACCESS_TOKEN] = auth.accessToken!!
            pref[USER_IS_AUTHENTICATED] = auth.isAuthenticated
            pref[FULL_NAME] = auth.fullName!!
            pref[GUARD_ID] = auth.guardId!!
        }
    }


    suspend fun clearAuthData() {
        context.authDataStore.edit { preferences ->
            preferences.clear()
        }
    }


}

