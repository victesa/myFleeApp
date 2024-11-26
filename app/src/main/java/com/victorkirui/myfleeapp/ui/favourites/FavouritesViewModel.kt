package com.victorkirui.myfleeapp.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.myfleeapp.data.MyRepository
import com.victorkirui.myfleeapp.ui.cart.CartProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val myRepository: MyRepository): ViewModel() {

    // StateFlow to expose cart items
    private val _favouriteItems = MutableStateFlow<List<FavouriteProducts>>(emptyList())
    val favouriteItems = _favouriteItems.asStateFlow()


    init {
        getAllFavouriteItems()
    }

    // Fetch all cart items from the repository
    private fun getAllFavouriteItems() {
        viewModelScope.launch {
            myRepository.getFavouriteItems().collect { items ->
                // Mapping CartItemsEntity to Product and calculating total price
                val products = items.map { favouriteItem ->
                    FavouriteProducts(
                        id = favouriteItem.id,
                        price = favouriteItem.price.toString(),
                        productName = favouriteItem.productName,
                        description = favouriteItem.productDescription,
                        imageUrl = favouriteItem.imageUrl,
                        sizes = favouriteItem.productSizes,
                    )
                }
                _favouriteItems.value = products
            }
        }
    }
}