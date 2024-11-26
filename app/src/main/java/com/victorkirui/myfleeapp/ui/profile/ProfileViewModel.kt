package com.victorkirui.myfleeapp.ui.profile

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.victorkirui.myfleeapp.data.UserDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDetailsRepository: UserDetailsRepository,
    private val firebase: Firebase,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _userData = MutableStateFlow(UserData("", "", ""))
    val userData = _userData.asStateFlow()

    init {
        loadUserData()
        Log.d("UserDetailsRepository", "DataStore initialized: $dataStore")
        Log.d("userInfo", "DataStore initialized: ${userData.value}")
    }

    // Function to read data from the repository and update the state
    private fun loadUserData() {
        viewModelScope.launch {
            userDetailsRepository.readData.collect { userData ->
                _userData.value = userData // Update the state flow with UserData object
            }
        }
    }

    fun signOut(onSignOutCompleteListener: (Boolean) -> Unit) {
        firebase.auth.signOut()
        onSignOutCompleteListener(true)
    }


}

data class UserData(val firstName: String, val lastName: String, val emailAddress: String)