package com.victorkirui.myfleeapp.ui.authentication

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.victorkirui.myfleeapp.data.UserDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(private val userDetailsRepository: UserDetailsRepository) : ViewModel() {

    private var auth = Firebase.auth

    private var _emailAddress = mutableStateOf("")
    var emailAddress: State<String> = _emailAddress

    private var _password = mutableStateOf("")
    var password: State<String> = _password

    private var _confirmPassword = mutableStateOf("")
    var confirmPassword: State<String> = _confirmPassword

    private var _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    private var firstName = ""
    private var lastName = ""

    // Update functions
    fun onEmailChange(newEmail: String) {
        _emailAddress.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    // Credential Verification Functions
    private fun verifyPasswordCredentials(): Boolean {
        return password.value == confirmPassword.value
    }

    private fun verifyEmail(): Boolean {
        val email = emailAddress.value
        if (!email.endsWith("@strathmore.edu")) {
            return false
        }

        val namePart = email.substringBefore("@")
        val parts = namePart.split(".")

        firstName = parts[0]
        lastName = parts[1]

        return parts.size == 2
    }

    // Create Account Function
    fun createAccount(onSignUpCompleteListener: (AuthStatus) -> Unit){
        if (verifyPasswordCredentials() && verifyEmail()) {
            auth.createUserWithEmailAndPassword(emailAddress.value, password.value)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _errorMessage.value = ""
                        viewModelScope.launch {
                            userDetailsRepository.saveData(firstName, lastName, emailAddress.value)
                        }
                        onSignUpCompleteListener(AuthStatus.SUCCESS)
                    } else {
                        _errorMessage.value = task.exception?.message ?: "Unknown error occurred"
                        onSignUpCompleteListener(AuthStatus.FAILED)
                    }
                }
        } else {
            _errorMessage.value = "Invalid credentials. Please check your inputs."
            onSignUpCompleteListener(AuthStatus.FAILED)
        }
    }

    // Sign In Function
    fun signIn(onSignInCompleteListener: (AuthStatus) -> Unit) {
        if (verifyEmail() && password.value.isNotEmpty()) {
            auth.signInWithEmailAndPassword(emailAddress.value, password.value)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _errorMessage.value = ""
                        viewModelScope.launch {
                            userDetailsRepository.saveData(firstName, lastName, emailAddress.value)
                        }
                        onSignInCompleteListener(AuthStatus.SUCCESS)
                    } else {
                        _errorMessage.value = task.exception?.message ?: "Unknown error occurred"
                        onSignInCompleteListener(AuthStatus.FAILED)
                    }
                }
        } else {
            _errorMessage.value = "Invalid credentials. Please check your inputs."
            onSignInCompleteListener(AuthStatus.FAILED)
        }
    }
}
