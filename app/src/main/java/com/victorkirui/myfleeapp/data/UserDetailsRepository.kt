package com.victorkirui.myfleeapp.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.victorkirui.myfleeapp.ui.profile.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDetailsRepository @Inject constructor(private var dataStore: DataStore<Preferences>) {
    companion object {
        private val FIRSTNAME = stringPreferencesKey("firstName")
        private val LASTNAME = stringPreferencesKey("lastName")
        private val EMAILADDRESS = stringPreferencesKey("emailAddress")
    }

    // Function to save user details
    suspend fun saveData(firstName: String, lastName: String, emailAddress: String) {
        Log.d("savedData", firstName)
        dataStore.edit { preferences ->
            preferences[FIRSTNAME] = firstName
            preferences[LASTNAME] = lastName
            preferences[EMAILADDRESS] = emailAddress
        }
    }

    // Flow to read the user details (all three fields)
    val readData: Flow<UserData> = dataStore.data
        .map { preferences ->
            UserData(
                firstName = preferences[FIRSTNAME] ?: "",
                lastName = preferences[LASTNAME] ?: "",
                emailAddress = preferences[EMAILADDRESS] ?: ""
            )
        }
}
