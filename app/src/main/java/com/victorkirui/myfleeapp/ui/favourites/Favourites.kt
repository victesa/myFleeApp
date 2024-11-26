package com.victorkirui.myfleeapp.ui.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.victorkirui.myfleeapp.R
import com.victorkirui.myfleeapp.ui.authentication.components.shimmerEffect

@Composable
fun FavouritesScreen(favouritesViewModel: FavouritesViewModel = hiltViewModel()){

    val favouriteProducts by favouritesViewModel.favouriteItems.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF101D25))
        .padding(16.dp)
    ){

        Box(modifier = Modifier.padding(top = 32.dp).fillMaxHeight(.1f),
            contentAlignment = Alignment.Center){
            Text(text = "Favourites",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold)
        }




        if (favouriteProducts.isEmpty()){
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center){
                Text(text = "Empty", fontSize = 16.sp, color = Color.White)
            }
        }else{
            LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)){
                items(favouriteProducts.size){
                    FavouriteProductCard(
                        imageUrl = favouriteProducts[it].imageUrl,
                        productName = favouriteProducts[it].productName,
                        price = favouriteProducts[it].price,
                        onClick = {}
                    )
                }
            }
        }


    }
}


@Composable
fun FavouriteProductCard(
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
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(15.dp))
                    .clickable {
                        onClick()
                    }

            )
        }

        Text(text = productName, maxLines = 2, color = Color(0xFFE8E8E8),
            fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(8.dp))

        Text(text = "$price/=", color = Color.White, fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp, start = 8.dp))
    }

}
