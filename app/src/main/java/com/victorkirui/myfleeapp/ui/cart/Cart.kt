package com.victorkirui.myfleeapp.ui.cart

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.victorkirui.myfleeapp.R
import com.victorkirui.myfleeapp.domain.TransactionStatus

@Composable
fun CartScreen(navigateBackToHomeScreen:() -> Unit,
               cartViewModel: CartViewModel = hiltViewModel()
){
    val cartItems by cartViewModel.cartItems.collectAsState()
    val isDialogVisible by cartViewModel.dialogIsVisible.collectAsState()
    Log.d("DialogState", "isDialogVisible observed: $isDialogVisible")

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF101D25))
            .padding(16.dp)){

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .padding(top = 16.dp), contentAlignment = Alignment.Center){
                Row(modifier = Modifier.fillMaxWidth()){
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White,
                        modifier = Modifier.clickable {
                            navigateBackToHomeScreen()
                        })
                }

                Text(text = "My Cart", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            if (cartItems.isEmpty()){
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center){
                    Text(text = "Cart is Empty", fontSize = 16.sp, color = Color.White)
                }
            }else{
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.8f)){
                    items(cartItems.size){
                        CartItem(
                            title = cartItems[it].productName,
                            price = cartItems[it].price,
                            size = cartItems[it].sizes,
                            onRemoveClick = {
                                cartViewModel.decreaseCartItemQuantity(cartItems[it].id)
                            },
                            onAddClick = {
                                cartViewModel.increaseCartItemQuantity(cartItems[it].id)
                            },
                            imageUrl = cartItems[it].imageUrl,
                            quantity = cartItems[it].quantity,
                            onLongClicked = {
                                cartViewModel.onLongClicked(it)
                            }
                        )
                        Spacer(modifier = Modifier.padding(top =16.dp))
                    }
                }

                val totalPrice = cartViewModel.totalPrice.collectAsState()

                val context = LocalContext.current

                BottomPart(totalPrice.value.toString(),
                    orderProducts = { cartViewModel.deleteAllCartItems(
                        onTransactionSuccess = {
                            if(it == TransactionStatus.SUCCESS){
                                navigateBackToHomeScreen()
                                Toast.makeText(context, "Your order has been placed", Toast.LENGTH_LONG).show()
                            }
                        }
                    ) })
            }

        }

        if (isDialogVisible){
            DeleteCartItemDialog(
                onDismissRequest = {
                    cartViewModel.onDismissDialog()
                },
                onConfirmButtonPressed = {
                    cartViewModel.deleteCartItem()
                }
            )
        }
    }
}

@Composable
fun BottomPart(totalPrice: String,
               orderProducts: () -> Unit){
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround){
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Total", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)

            Text(text = totalPrice, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }

        Button(modifier = Modifier.fillMaxWidth(),
            onClick = {orderProducts()},
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F53EF))){
            Text(text = "Buy now", modifier = Modifier.padding(8.dp), fontSize = 16.sp)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartItem(
    title: String,
    size: String ,
    price: String,
    quantity: Int = 1,
    onAddClick: () -> Unit = {},
    onRemoveClick: () -> Unit = {},
    imageUrl: String,
    onLongClicked:() -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
            .combinedClickable(
                onLongClick = {
                    onLongClicked()
                },
                onClick = {}
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF354046),
            contentColor = Color.White
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.3f)
                    .clip(RoundedCornerShape(10.dp))
            ) {

                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                )

            }

            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = title,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = size,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = price,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Card(modifier = Modifier
                        .width(80.dp)
                        .height(intrinsicSize = IntrinsicSize.Min),
                        colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.minus),
                                contentDescription = "Remove",
                                modifier = Modifier
                                    .clickable { onRemoveClick() }
                                    .size(20.dp),
                                tint = Color.Black
                            )

                            Text(text = quantity.toString(), fontSize = 15.sp, color = Color.Black)

                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = "Add",
                                modifier = Modifier
                                    .clickable { onAddClick() }
                                    .size(20.dp),
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewCartItem(){
    Column(modifier = Modifier.fillMaxSize()){
        CartScreen({})

    }
}