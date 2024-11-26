package com.victorkirui.myfleeapp.ui.authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.victorkirui.myfleeapp.ui.authentication.components.EmailTextField
import com.victorkirui.myfleeapp.ui.authentication.components.PasswordTextField

@Composable
fun SignInScreen(viewModel: AuthenticationViewModel = hiltViewModel(),
                 navigateToSignUpScreen: () -> Unit,
                 navigateToHomeScreen: () -> Unit) {
    // Define the gradient brush
    val gradientBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFF1A2A35), // Lighter color for the top-right
            Color(0xFF101D25) // Darker color dominating the rest
        ),
        center = Offset(800f, 0f), // Position the lighter color in the top-right
        radius = 500f // Set the radius to keep the gradient localized
    )

    val errorMessage = viewModel.errorMessage.value // Observe the error message state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush) // Apply the gradient background
            .padding(24.dp)
    ) {
        WelcomeSection()

        Spacer(modifier = Modifier.height(16.dp))


        SignInCredentialSection(
            email = viewModel.emailAddress.value,
            onEmailChange = { viewModel.onEmailChange(it) },
            password = viewModel.password.value,
            onPasswordChange = { viewModel.onPasswordChange(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current

        SignInBottomPart(signIn = {
            viewModel.signIn { authStatus ->
                if (authStatus == AuthStatus.SUCCESS) {
                   navigateToHomeScreen()
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        },
            navigateToSignUpScreen = {
                navigateToSignUpScreen()
            })
    }
}


@Composable
fun WelcomeSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign In",
            fontSize = 40.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )


    }
}

@Composable
fun SignInCredentialSection(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
    ) {
        EmailTextField(value = email, onValueChange = onEmailChange)

        PasswordTextField(value = password, onValueChange = onPasswordChange)
    }
}



@Composable
fun SignInBottomPart(signIn: () -> Unit,
                     navigateToSignUpScreen:() -> Unit) {
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account?", color = Color.White, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navigateToSignUpScreen() }) {
                Text(
                    text = "Register",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Button(
            onClick = { signIn() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F53EF))
        ) {
            Text(
                text = "Sign In",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

