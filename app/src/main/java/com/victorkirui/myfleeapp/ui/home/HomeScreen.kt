package com.victorkirui.myfleeapp.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.victorkirui.myfleeapp.data.Product
import com.victorkirui.myfleeapp.R
import com.victorkirui.myfleeapp.data.Store
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToProductDetailsScreen: (String) -> Unit,
    padding: PaddingValues
) {
    val popularProducts by homeViewModel.popularProducts.collectAsState()
    val newArrivals by homeViewModel.newArrivals.collectAsState()
    val bestSellingStores by homeViewModel.bestSellingStores.collectAsState()
    val isConnected by homeViewModel.isConnectedToInternet.collectAsState()
    val showOfflineWarning by homeViewModel.showOfflineWarning.collectAsState()
    val showNoDataWarning by homeViewModel.showNoDataWarning.collectAsState()

    val searchBarText by homeViewModel.searchBarText.collectAsState() // Search bar text
    val searchedProducts by homeViewModel.searchedProducts.collectAsState() // Search results

    var selectedCategory by rememberSaveable { mutableStateOf("Trending") }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val currentVisibleCategory = remember { mutableStateOf("Trending") }
    val focusManager = LocalFocusManager.current

    val isSearchActive = searchBarText.isNotEmpty() // Track if the user is searching

    var isBackPressedOnce by remember { mutableStateOf(false) }

    // BackHandler to clear search when back button is pressed
    BackHandler {
        if (searchBarText.isNotEmpty()) {
            // Clear the search text and search results
            homeViewModel.onSearchBarTextChange("")
            homeViewModel.clearSearchProducts()

            // Set the state to indicate that the back button has been pressed
            isBackPressedOnce = true
        } else if (isBackPressedOnce) {
            // Exit the app if back is pressed again after clearing search
            exitProcess(0) // Exits the app
        } else {
            // Otherwise, just update the state to show back pressed once
            isBackPressedOnce = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        if (showOfflineWarning) {
            Text("No internet", fontSize = 15.sp, color = Color.Gray)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101D25))
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.05f))

            HomeScreenSearchBar(
                searchText = searchBarText,
                onSearchTextChange = { homeViewModel.onSearchBarTextChange(it) },
                onSearchButtonClicked = {
                    homeViewModel.onSearchButtonClicked()
                }
            )

            if (searchedProducts.isNotEmpty()){
                androidx.compose.animation.AnimatedVisibility(
                    visible = isSearchActive,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 }, // Slide from the top
                        animationSpec = tween(durationMillis = 300)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it / 2 }, // Slide out towards the top
                        animationSpec = tween(durationMillis = 300)
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(searchedProducts.size) { product ->
                            ProductCard(
                                productName = searchedProducts[product].productName,
                                onClick = {
                                    navigateToProductDetailsScreen(searchedProducts[product].id)
                                },
                                price = searchedProducts[product].price,
                                imageUrl = searchedProducts[product].imageUrl,
                                isLoading = false
                            )
                        }
                    }
                }
            }else{
                // Default behavior when no search results
                Spacer(modifier = Modifier.height(16.dp))

                if (showNoDataWarning) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Connect to Internet", fontSize = 16.sp, color = Color.Gray)
                    }
                } else {
                    HomeScreenCategoryTabs(selectedCategory) { category ->
                        selectedCategory = category
                        val scrollToIndex = when (category) {
                            "Trending" -> 0
                            "New Arrival" -> 1
                            "Best Sellers" -> 2
                            else -> 0
                        }
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(scrollToIndex)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            TrendingProductsSection(isConnected, popularProducts.isNotEmpty(),
                                mostPopularProducts = popularProducts,
                                navigateToProductDetailsScreen = {
                                    navigateToProductDetailsScreen(it)
                                })
                        }
                        item {
                            NewArrivalsSection(isConnected, newArrivals.isNotEmpty(), newArrivalsList = newArrivals,
                                navigateToProductDetailsScreen = navigateToProductDetailsScreen)
                        }
                        item {
                            BestRatedProductsSection(isConnected, bestSellingStores.isNotEmpty(),
                                bestSellersList = bestSellingStores)
                        }
                    }
                }
            }

            LaunchedEffect(remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }) {
                val visibleCategory = when (lazyListState.firstVisibleItemIndex) {
                    0 -> "Trending"
                    1 -> "New Arrival"
                    2 -> "Best Sellers"
                    else -> "Trending"
                }

                if (currentVisibleCategory.value != visibleCategory) {
                    currentVisibleCategory.value = visibleCategory
                    selectedCategory = visibleCategory
                }
            }
        }
    }
}




@Composable
fun TrendingProductsSection(isConnected: Boolean, isDataAvailable: Boolean,
                            mostPopularProducts: List<Product>,
                            navigateToProductDetailsScreen: (String) -> Unit){
    val spacerHeight = LocalConfiguration.current.screenHeightDp.dp * 0.05f
    PromoCarousel()

    Spacer(modifier = Modifier.height(spacerHeight))

    if (isConnected && !isDataAvailable){
        TrendingProducts(isLoading = true,
            mostPopularProducts = mostPopularProducts,
            navigateToProductDetailsScreen = {
                //Nothing Happens
            })
    }else{
        TrendingProducts(isLoading = false,
            mostPopularProducts = mostPopularProducts,
            navigateToProductDetailsScreen = {
                navigateToProductDetailsScreen(it)
            })

    }

}

@Composable
fun TrendingProducts(isLoading: Boolean,
                     mostPopularProducts: List<Product>,
                     navigateToProductDetailsScreen:(String) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Most Popular",
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
            items(mostPopularProducts.size) { index ->
                ProductCard(
                    isLoading,
                    imageUrl = mostPopularProducts[index].imageUrl,
                    productName = mostPopularProducts[index].productName,
                    price = mostPopularProducts[index].price,
                    onClick = {navigateToProductDetailsScreen(mostPopularProducts[index].id)})
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}


@Composable
fun NewArrivalsSection(isConnected: Boolean, isDataAvailable: Boolean,
                       newArrivalsList: List<Product>,
                       navigateToProductDetailsScreen: (String) -> Unit){
    val height = LocalConfiguration.current.screenHeightDp.dp * 0.05f

    Column(modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 16.dp)){

        NewArrivalCard()

        Spacer(modifier = Modifier.height(height))

        if (isConnected && !isDataAvailable){
            NewArrivalsItems(isLoading = true,
                newArrivalsList = newArrivalsList,
                onClick = {navigateToProductDetailsScreen(it)})
        }else{
            NewArrivalsItems(isLoading = false,
                newArrivalsList = newArrivalsList,
                onClick = {navigateToProductDetailsScreen(it)})

        }

    }
}



@Composable
fun HomeScreenCategoryTabs(clicked: String, onClick:(String) -> Unit) {
    val categories = listOf("Trending", "New Arrival", "Best Sellers")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(categories.size) {
            Text(
                text = categories[it],
                color = Color.White,
                modifier = Modifier
                    .background(
                        if (categories[it] == clicked) Color(0xFF4F53EF) else Color(0xFF354046),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 10.dp)
                    .clickable {
                        onClick(categories[it])
                    },
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun PromoCarousel() {
    val promoItems = listOf(
        PromoItem(
            title = "Try New Fabrics",
            description = "Tons of fabrics",
            backgroundColor = Color(0xFFD5C4FC),
            imageResource = R.drawable.clothes,
            promoDeals = "20% off"
        ),
        PromoItem(
            title = "Buy Accessories",
            description = "New and affordable products",
            backgroundColor = Color(0xFFB3E5FC), // Light blue for events
            imageResource = R.drawable.accessory,
            promoDeals = "Hot deals"
        ),
        PromoItem(
            title = "Elevate your looks",
            description = "Buy stunning jewelry",
            backgroundColor = Color(0xFFFEE4C4), // Light beige for responsibility
            imageResource = R.drawable.jewelry,
            promoDeals = "Hot Deals"
        )
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(promoItems.size) {
            PromoCard(promoItems[it])
        }
    }
}


@Composable
fun BestRatedProductsSection(isConnected: Boolean, isDataAvailable: Boolean,
                       bestSellersList: List<Store>){
    Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp)){
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically){
                Box(
                    modifier = Modifier
                        .size(40.dp) // Ensures the box is circular
                        .background(color = Color(0xFF354046), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Text(
                    text = "Best Rated",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 8.dp)
                )

            }

            Text(
                text = "Show all",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        if (isConnected && isDataAvailable){
            LazyRow(modifier = Modifier.padding(top = 16.dp)){
                items(3){
                    BestSellersCard(isLoading = false)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }else{
            LazyRow(modifier = Modifier.padding(top = 16.dp)){
                items(3){
                    BestSellersCard(isLoading = true)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

