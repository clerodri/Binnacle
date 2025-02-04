package com.clerodri.binnacle.auth.data.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
        private val LOCALITY = stringPreferencesKey("localityId")
        private val USER_ID = stringPreferencesKey("id")
        private val USER_IS_AUTHENTICATED = booleanPreferencesKey("isAuthenticated")
    }

    val userData: Flow<UserData?> = context.authDataStore.data.map { preferences ->
        val id = preferences[USER_ID]
        val localityId = preferences[LOCALITY]
        val fullname = preferences[FULL_NAME]
        val accessToken = preferences[ACCESS_TOKEN]
        val refreshToken = preferences[REFRESH_TOKEN]
        val isAuthenticated = preferences[USER_IS_AUTHENTICATED] ?: false

        if (id != null && localityId != null && fullname != null && accessToken != null
            && refreshToken != null
        ) {
            UserData(
                id, fullname,localityId, accessToken, refreshToken, isAuthenticated
            )
        } else {
            null
        }
    }

    suspend fun saveUserData(user: UserData) {
        context.authDataStore.edit { pref ->
            pref[ACCESS_TOKEN] = user.accessToken
            pref[REFRESH_TOKEN] = user.refreshToken
            pref[FULL_NAME] = user.fullname
            pref[LOCALITY] = user.localityId
            pref[USER_ID] = user.id
            pref[USER_IS_AUTHENTICATED] = user.isAuthenticated

        }
    }

    suspend fun clearUserData() {
        context.authDataStore.edit { preferences ->
            preferences.clear()
//            preferences.remove(ACCESS_TOKEN)
//            preferences.remove(REFRESH_TOKEN)
//            preferences.remove(LOCALITY)
//            preferences.remove(FULL_NAME)
//            preferences.remove(USER_ID)
//            preferences.remove(USER_IS_AUTHENTICATED)
        }
    }
}

data class UserData(
    val id: String,
    val fullname: String,
    val localityId: String,
    val accessToken: String,
    val refreshToken: String,
    val isAuthenticated: Boolean
)