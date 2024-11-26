package com.victorkirui.myfleeapp.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun DeleteCartItemDialog(
    onDismissRequest:() -> Unit,
    onConfirmButtonPressed:() -> Unit,
){
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(onClick = {onConfirmButtonPressed()}){
                Text("yes", color = Color.Red, fontSize = 16.sp)
            }
        }
        , dismissButton = {
            TextButton(onClick = {onDismissRequest()}){
                Text("no", fontSize = 16.sp, color = Color.White)
            }
        },
        text = {
            Text("Are you sure you want to delete?", color = Color.White, fontSize = 16.sp)
        },

        icon = {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete cart item", tint = Color.Red)
        },
        containerColor = Color(0xFF354046)
    )
}

@Preview
@Composable
fun PreviewDeleteCartItemDialog(){
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        DeleteCartItemDialog({}) {  }
    }
}