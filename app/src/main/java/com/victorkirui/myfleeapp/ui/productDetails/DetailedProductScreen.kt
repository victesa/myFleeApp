package com.victorkirui.myfleeapp.ui.productDetails

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.victorkirui.myfleeapp.data.Product
import com.victorkirui.myfleeapp.domain.TransactionStatus
import com.victorkirui.myfleeapp.ui.authentication.components.shimmerEffect



@Composable
fun DetailedProductScreen(
    productId: String,
    navigateBackToHomeScreen:() -> Unit,
    detailedViewModel: DetailedViewModel = hiltViewModel()
) {
    // Launch effect to call getProductDetails when productId changes
    LaunchedEffect(productId) {
        detailedViewModel.getProductDetails(productId)
    }

    val context = LocalContext.current

    // Observe the product details and transaction status
    val productDetails by detailedViewModel.productDetails.collectAsState()
    val productSize by detailedViewModel.productSize.collectAsState()
    val transactionStatus by detailedViewModel.transactionStatus.collectAsState()

    val isFavourite by detailedViewModel.isFavourite.collectAsState()

    // Handle the UI based on transaction status
    if (transactionStatus == TransactionStatus.SUCCESS) {
        // Show Toast for success
        Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show()

        // Navigate to another screen after a successful transaction
        LaunchedEffect(Unit) {
            navigateBackToHomeScreen()
        }
    } else if (transactionStatus == TransactionStatus.FAILURE) {
        Toast.makeText(context, "Failed to add item to cart", Toast.LENGTH_SHORT).show()
    }

    if (productDetails != null) {
        // Pass the product details to the content
        DetailedProductContent(
            product = productDetails!!,
            navigateBackToHomeScreen = navigateBackToHomeScreen,
            addItemToCart = {
                detailedViewModel.addItemToCart()  // Calls addItemToCart in the ViewModel
            },
            selectedSize = productSize,
            onSelectedSizeChange = { newSize ->
                detailedViewModel.changeProductSize(newSize)
            },
            onFavouriteButtonClicked = {
                detailedViewModel.onFavouriteButtonClicked()
            },
            isFavorite = isFavourite
        )
    } else {
        // Display loading or placeholder UI while data is being fetched
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFF101D25))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = "",  // Placeholder or null
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .shimmerEffect() // Apply shimmer effect for loading
                    )
                }

                // Placeholder for size selection and product details section
                SizeSection(emptyList(),
                    selectedSize = productSize,
                    onSelectedSizeChange = {
                        detailedViewModel.changeProductSize(it)
                    })
                ProductDetailsSection("")
            }
            // Add to Cart button at the bottom
            AddToCartButton(addItemToCart = {
                detailedViewModel.addItemToCart()  // Same as above for adding the item to cart
            })
        }
    }
}


@Composable
fun DetailedProductContent(product: Product, navigateBackToHomeScreen: () -> Unit,
                           addItemToCart: () -> Unit,
                           onSelectedSizeChange: (String) -> Unit,
                           selectedSize: String,
                           onFavouriteButtonClicked: () -> Unit,
                           isFavorite: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF101D25))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Image with Back Button
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            modifier = Modifier.size(35.dp)
                                .clickable { navigateBackToHomeScreen() },
                            tint = Color.White
                        )
                        Text(
                            text = "Back",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = {
                            onFavouriteButtonClicked()
                        },
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .border(width = 1.dp, color = if (isFavorite)Color(0xFF4F53EF) else Color.White , shape = CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite)Color(0xFF4F53EF) else Color.White
                        )
                    }
                }
            }

            ProductBasicInfo(productName = product.productName, price = product.price)

            // Size Selection Section
            SizeSection(sizeList = product.sizes,
                selectedSize = selectedSize,
                onSelectedSizeChange = {onSelectedSizeChange(it)})// Product Details SectionProductDetailsSection(productDetails = product.description)

            ProductDetailsSection(productDetails = product.description)
        }

        // Add to Cart Button anchored at the bottom
        AddToCartButton(addItemToCart)
    }
}

@Composable
fun ProductBasicInfo(price: String, productName: String){
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp, top = 20.dp)){
        Text(text = productName, fontSize = 23.sp, color = Color.White, fontWeight = FontWeight.SemiBold)

        Text(text = "Price:$price/=", fontSize = 18.sp, color = Color.White,fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SizeSection(sizeList: List<String>,
                selectedSize: String,
                onSelectedSizeChange: (String) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Size",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        LazyRow(modifier = if (sizeList.isEmpty()){Modifier.padding(top = 8.dp).fillMaxWidth().shimmerEffect()} else{Modifier.padding(top = 8.dp).fillMaxWidth()}) {
            items(sizeList.size) { index ->
                val size = sizeList[index]
                Box(
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = if (size == selectedSize) Color(0xFF4F53EF) else Color.White,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectedSizeChange(size) }
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = size,
                        color = if (size == selectedSize) Color(0xFF4F53EF) else Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@Composable
fun ProductDetailsSection(productDetails: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Product Details",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = productDetails,
            color = Color.Gray,
            fontSize = 16.sp,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
            modifier = if(productDetails.isEmpty())Modifier.padding(top = 8.dp).shimmerEffect() else Modifier.padding(top = 8.dp)
        )

        Text(
            text = if (expanded) "Read Less" else "Read More",
            color = Color(0xFF4F53EF),
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { expanded = !expanded }
        )
    }
}

@Composable
fun AddToCartButton(addItemToCart:()-> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = { addItemToCart() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F53EF))
        ) {
            Text(
                text = "Add To Cart",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

