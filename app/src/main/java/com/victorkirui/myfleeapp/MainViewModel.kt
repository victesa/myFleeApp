package com.victorkirui.myfleeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.victorkirui.myfleeapp.nav.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val firebaseAuth: FirebaseAuth = Firebase.auth

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    private val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _firstScreen = MutableStateFlow(Routes.AuthenticationNavGraph.route)
    val firstScreen = _firstScreen.asStateFlow()

    init {
        viewModelScope.launch {
            checkAuthenticationStatus()
        }
    }

    private fun checkAuthenticationStatus() {
        _currentUser.value = firebaseAuth.currentUser
        _isLoading.value = true

        if (currentUser.value != null){
            _firstScreen.value = Routes.AuthenticatedNavGraph.route
            _isLoading.value = false
        }else{
            _firstScreen.value = Routes.AuthenticationNavGraph.route
            _isLoading.value = false

        }
    }
}

