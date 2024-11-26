package com.victorkirui.myfleeapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.victorkirui.myfleeapp.data.Product
import com.victorkirui.myfleeapp.R
import com.victorkirui.myfleeapp.ui.authentication.components.shimmerEffect

@Composable
fun NewArrivalCard(){
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp), colors = CardDefaults.cardColors(Color(0xFF009056)),
        shape = RoundedCornerShape(20.dp)
    ){
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically){
            Column(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(.8f), verticalArrangement = Arrangement.Center){
                Text(text = "New Arrivals", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text(text = "New products posted", style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFE8E8E8),
                    fontWeight = FontWeight.Light)
            }

            Box(modifier = Modifier
                .size(56.dp)
                .background(color = Color(0xFFD2FDE1), shape = CircleShape),
                contentAlignment = Alignment.Center){
                Icon(painter = painterResource(R.drawable.incoming),
                    contentDescription = null, tint = Color(0xFF009056),
                    modifier = Modifier.size(20.dp))
            }
        }
    }
}


@Composable
fun HomeScreenSearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchButtonClicked: () -> Unit
) {
    // Track whether the search field is focused
    val isFocused = remember { mutableStateOf(false) }


    // FocusRequester to manage focus for the TextField
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        value = searchText,
        onValueChange = { onSearchTextChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp)
            .background(color = Color(0xFF354046), shape = RoundedCornerShape(23.dp))
            .onFocusChanged { focusState ->
                isFocused.value = focusState.isFocused
            }
            .focusRequester(focusRequester), // Attach focusRequester
        textStyle = TextStyle(
            color = Color.White,
            fontSize = if (isFocused.value) 20.sp else 18.sp, // Slightly smaller font when not focused
            fontWeight = FontWeight.SemiBold
        ),
        cursorBrush = Brush.verticalGradient(
            colors = listOf(Color.Gray, Color.Gray)
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchButtonClicked() // Trigger search when user presses the search button on keyboard
            }
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Icon with Circular Background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = Color(0xFF4F53EF), shape = CircleShape)
                        .clickable { onSearchButtonClicked() }, // Search button click
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp)) // Add spacing between the icon and the input

                // Show placeholder text only when the search bar is not focused and the text is empty
                if (!isFocused.value && searchText.isEmpty()) {
                    // Placeholder when text is empty
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Search on MyFleeApp",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Clothes. Accessories. Anything",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                } else {
                    // Display actual input text when user types
                    innerTextField() // This will display the user's input text in white
                }
            }
        }
    )
}


@Composable
fun PromoCard(promo: PromoItem) {

    val width = LocalConfiguration.current.screenWidthDp.dp * 0.8f
    Box(
        modifier = Modifier
            .width(width)
            .height((width / 1.75f))
            .clip(RoundedCornerShape(16.dp))
            .background(promo.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(.6f)) {

                Column(modifier = Modifier.fillMaxHeight(.7f)){
                    Text(
                        text = promo.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = promo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        maxLines = 1
                    )
                }


                Text(
                    text = promo.promoDeals,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier
                        .background(color = Color(0xFF101D25), shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Image(
                painter = painterResource(id = promo.imageResource),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}

data class PromoItem(
    val title: String,
    val description: String,
    val backgroundColor: Color,
    val imageResource: Int,
    val promoDeals: String
)

@Composable
fun ProductCard(isLoading: Boolean,
                imageUrl: String,
                productName: String,
                price: String,
                onClick:() -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.35f

    Column(modifier = Modifier.width(cardWidth)){
        Box(modifier = Modifier
            .background(color = Color(0xFF354046), shape = RoundedCornerShape(15.dp))
            .size(cardWidth),
            contentAlignment = Alignment.Center){
            AsyncImage(
                model = imageUrl,
                contentDescription = "Product Picture",
                contentScale = ContentScale.Fit,
                modifier = if (isLoading){
                    Modifier
                        .shimmerEffect()
                        .aspectRatio(1f)}else{
                    Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(15.dp))
                        .clickable {
                            onClick()
                        }}

            )
        }

        Text(text = productName, maxLines = 2, color = Color(0xFFE8E8E8),
            fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = if (isLoading){
                Modifier
                    .padding(8.dp)
                    .shimmerEffect()}else{Modifier.padding(8.dp)})

        Text(text = "$price/=", color = Color.White, fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp, modifier = if (isLoading){
                Modifier
                    .padding(top = 4.dp, start = 8.dp)
                    .shimmerEffect()}else{Modifier.padding(top = 4.dp, start = 8.dp)})
    }

}

@Composable
fun BestSellersCard(isLoading: Boolean) {
    val width = LocalConfiguration.current.screenWidthDp.dp * 0.9f
    Card(
        modifier = Modifier
            .width(width)
            .wrapContentHeight(), // Let the content dictate the height
        colors = CardDefaults.cardColors(Color(0xFF08151d)),
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .height(IntrinsicSize.Min) // Match height based on the tallest child
        ) {
            // Left Image Section
            Box(
                modifier = if (isLoading){
                    Modifier
                        .background(color = Color(0xFF354046), shape = RoundedCornerShape(15.dp))
                        .fillMaxHeight()
                        .weight(0.4f)
                        .shimmerEffect()
                }else{
                    Modifier
                        .background(color = Color(0xFF354046), shape = RoundedCornerShape(15.dp))
                        .fillMaxHeight()
                        .weight(0.4f)
                },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.clothes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }

            // Right Text Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f) // Occupy 60% of the width
                    .padding(start = 16.dp, end = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "CLOTHES",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "Teletubbies Baby Doll Key Chain",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFF939598),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp),
                        lineHeight = 17.sp
                    )

                    Text(
                        text = "KES 2000.00",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = Color.Gray
                    )

                    Text(
                        text = "Lisa Wanjiku",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NewArrivalsItems(isLoading: Boolean,
                     newArrivalsList: List<Product>,
                     onClick:(String) -> Unit){
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "New Arrivals",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Show all",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(16.dp)
        ) {
            items(newArrivalsList.size) { index ->
                ProductCard(isLoading,
                    imageUrl = newArrivalsList[index].imageUrl,
                    productName = newArrivalsList[index].productName,
                    price = newArrivalsList[index].price,
                    onClick = { onClick(newArrivalsList[index].id) }
                    )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}
