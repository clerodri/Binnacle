package com.clerodri.binnacle.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.clerodri.binnacle.auth.presentation.guard.GuardScreenState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val AUTH_DATASTORE = "auth_data"

val Context.authDataStore by preferencesDataStore(name = AUTH_DATASTORE)

class AuthPreferences @Inject constructor(

    @ApplicationContext private val context: Context
) {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        private val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
        private val FULL_NAME = stringPreferencesKey("fullname")
        private val LOCALITY = stringPreferencesKey("localityId")
        private val USER_ID = stringPreferencesKey("id")
    }

    val userData: Flow<UserData?> = context.authDataStore.data.map { preferences ->
        val id = preferences[USER_ID]
        val localityId = preferences[LOCALITY]
        val fullname = preferences[FULL_NAME]
        val accessToken = preferences[ACCESS_TOKEN]
        val refreshToken = preferences[REFRESH_TOKEN]

        if (id != null && localityId != null && fullname != null && accessToken != null
            && refreshToken != null
        ) {
            UserData(
                id, fullname,localityId, accessToken, refreshToken
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

        }
    }

    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
            preferences.remove(LOCALITY)
            preferences.remove(FULL_NAME)
            preferences.remove(USER_ID)
        }
    }
}

data class UserData(
    val id: String,
    val fullname: String,
    val localityId: String,
    val accessToken: String,
    val refreshToken: String
)