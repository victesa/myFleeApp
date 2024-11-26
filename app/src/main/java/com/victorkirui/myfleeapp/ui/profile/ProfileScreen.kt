package com.victorkirui.myfleeapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.victorkirui.myfleeapp.R

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel = hiltViewModel(),
                  navigateToAuthenticationGraph:() -> Unit,
                  navigateBackToHomeScreen:() -> Unit){

    val userData by profileViewModel.userData.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF101D25)).padding(16.dp)){

        Box(modifier = Modifier.fillMaxWidth()
            .padding(vertical = 16.dp).padding(top = 16.dp), contentAlignment = Alignment.Center){
            Row(modifier = Modifier.fillMaxWidth()){
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White,
                    modifier = Modifier.clickable {
                        navigateBackToHomeScreen()
                    })
            }

            Text(text = "Profile", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }

        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(.3f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Image(painter = painterResource(R.drawable.pic),
                contentDescription = null,
                modifier = Modifier.clip(CircleShape)
                    .size(150.dp))
        }

        Text("Personal Info", fontSize = 24.sp, color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(32.dp))


        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(.8f)){
            TextDetailsSection(title = "First Name", info = userData.firstName)

            Spacer(modifier = Modifier.height(16.dp))

            TextDetailsSection(title = "Last Name", info = userData.lastName)

            Spacer(modifier = Modifier.height(16.dp))


            TextDetailsSection(title = "Email Address", info = userData.emailAddress)
        }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center){
            OutlinedButton(onClick = {
                profileViewModel.signOut { 
                    navigateToAuthenticationGraph()
                }
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, shape = RoundedCornerShape(10.dp),
                        color = Color.Red),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ){

                Text(text = "Sign Out", fontSize = 16.sp, color = Color.Red, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(8.dp))
            }
        }
    }
}


@Composable
fun TextDetailsSection(title: String, info: String){
    Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)

    Text(info, fontSize = 16.sp, color = Color.Gray)
}

