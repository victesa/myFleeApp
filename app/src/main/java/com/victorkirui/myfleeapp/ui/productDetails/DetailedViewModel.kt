package com.victorkirui.myfleeapp.ui.productDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.myfleeapp.data.MyRepository
import com.victorkirui.myfleeapp.data.Product
import com.victorkirui.myfleeapp.domain.TransactionStatus
import com.victorkirui.myfleeapp.ui.cart.CartProduct
import com.victorkirui.myfleeapp.ui.favourites.FavouriteProducts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel for product details
@HiltViewModel
class DetailedViewModel @Inject constructor(private val myRepository: MyRepository) : ViewModel() {

    private val _productDetails = MutableStateFlow<Product?>(null)
    val productDetails = _productDetails.asStateFlow()

    private val _transactionStatus = MutableStateFlow(TransactionStatus.IDLE)
    val transactionStatus = _transactionStatus.asStateFlow()

    private val _productSize = MutableStateFlow("")
    val productSize = _productSize.asStateFlow()

    private val _isFavourite = MutableStateFlow(false)
    val isFavourite = _isFavourite.asStateFlow()

    fun getProductDetails(id: String) {
        viewModelScope.launch {
            myRepository.getProductDetails(id).collect { product ->
                _productDetails.value = product

                // Check if the product is already a favourite
                val isFav = myRepository.getFavouriteItems()
                    .map { favItems -> favItems.any { it.id == product?.id } }
                    .firstOrNull() ?: false
                _isFavourite.value = isFav

                // Reset product size when new product is loaded
                _productSize.value = product?.sizes?.firstOrNull() ?: ""
            }
        }
    }

    fun onFavouriteButtonClicked() {
        viewModelScope.launch {
            if (isFavourite.value) {
                deleteFavouriteProduct()
            } else {
                addFavouriteProduct()
            }
        }
    }

    private suspend fun addFavouriteProduct() {
        val product = _productDetails.value ?: return
        val favourite = FavouriteProducts(
            id = product.id,
            price = product.price,
            productName = product.productName,
            description = product.description,
            imageUrl = product.imageUrl,
            sizes = product.sizes
        )

        myRepository.insertFavourites(favourite)
        myRepository.addFavouriteToFirebase(favourite)
        _isFavourite.value = true
    }

    private suspend fun deleteFavouriteProduct() {
        val product = _productDetails.value ?: return
        val favourite = FavouriteProducts(
            id = product.id,
            price = product.price,
            productName = product.productName,
            description = product.description,
            imageUrl = product.imageUrl,
            sizes = product.sizes
        )

        myRepository.deleteFavourites(favourite)
        myRepository.removeFavouriteToFirebase(favourite)
        _isFavourite.value = false
    }

    fun addItemToCart() {
        viewModelScope.launch {
            val product = _productDetails.value
            val selectedSize = _productSize.value
            if (product != null && selectedSize.isNotEmpty()) {
                myRepository.insertCartItem(
                    CartProduct(
                        id = product.id,
                        price = product.price,
                        productName = product.productName,
                        description = product.description,
                        imageUrl = product.imageUrl,
                        quantity = 1,
                        sizes = selectedSize
                    )
                )
                _transactionStatus.value = TransactionStatus.SUCCESS
            } else {
                _transactionStatus.value = TransactionStatus.FAILURE
            }
        }
    }

    fun changeProductSize(newSize: String) {
        _productSize.value = newSize
    }
}


