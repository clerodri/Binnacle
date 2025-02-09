package com.clerodri.binnacle.authentication.data.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.clerodri.binnacle.authentication.domain.model.UserData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val AUTH_DATASTORE = "auth_data"

val Context.authDataStore by preferencesDataStore(name = AUTH_DATASTORE)

class UserInformation @Inject constructor(

    @ApplicationContext private val context: Context
) {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        private val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
        private val FULL_NAME = stringPreferencesKey("fullname")
        private val LOCALITY_ID = intPreferencesKey("localityId")
        private val USER_ID = intPreferencesKey("id")
        private val USER_IS_AUTHENTICATED = booleanPreferencesKey("isAuthenticated")
    }

    val userData: Flow<UserData?> = context.authDataStore.data.map { preferences ->
        val id = preferences[USER_ID]
        val localityId = preferences[LOCALITY_ID]
        val fullname = preferences[FULL_NAME]
        val accessToken = preferences[ACCESS_TOKEN]
        val refreshToken = preferences[REFRESH_TOKEN]
        val isAuthenticated = preferences[USER_IS_AUTHENTICATED] ?: false

        if (id != null && localityId != null && fullname != null && accessToken != null
            && refreshToken != null
        ) {
            UserData(
                id, fullname, localityId, accessToken, refreshToken, isAuthenticated
            )
        } else {
            null
        }
    }

    suspend fun saveUserData(user: UserData) {
        context.authDataStore.edit { pref ->
            pref[ACCESS_TOKEN] = user.accessToken!!
            pref[REFRESH_TOKEN] = user.refreshToken!!
            pref[FULL_NAME] = user.fullname!!
            pref[LOCALITY_ID] = user.localityId!!
            pref[USER_ID] = user.id!!
            pref[USER_IS_AUTHENTICATED] = user.isAuthenticated

        }
    }

    suspend fun clearUserData() {
        context.authDataStore.edit { preferences ->
            Log.d("OO", "UserInformation clearUserData")

            preferences.clear()

            Log.d("OO", "UserInformation $preferences" )
        }
    }
}

