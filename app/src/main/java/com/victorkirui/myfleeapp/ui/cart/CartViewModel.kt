package com.victorkirui.myfleeapp.ui.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.myfleeapp.data.MyRepository
import com.victorkirui.myfleeapp.data.Product
import com.victorkirui.myfleeapp.domain.TransactionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val myRepository: MyRepository) : ViewModel() {

    // StateFlow to expose cart items
    private val _cartItems = MutableStateFlow<List<CartProduct>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    // StateFlow to calculate total price of all items in the cart
    private val _totalPrice = MutableStateFlow(0)
    val totalPrice = _totalPrice.asStateFlow()

    private val _dialogIsVisible = MutableStateFlow(false)
    val dialogIsVisible = _dialogIsVisible.asStateFlow()

    private val _clickedItemIndex = MutableStateFlow(0)
    val clickedItemIndex = _clickedItemIndex.asStateFlow()

    init {
        getAllCartItems()
    }

    // Fetch all cart items from the repository
    private fun getAllCartItems() {
        viewModelScope.launch {
            myRepository.getAllCartItems().collect { items ->
                // Mapping CartItemsEntity to Product and calculating total price
                val products = items.map { cartItem ->
                    CartProduct(
                        id = cartItem.id,
                        price = cartItem.price.toString(),
                        productName = cartItem.productName,
                        description = cartItem.productDescription,
                        imageUrl = cartItem.imageUrl,
                        sizes = cartItem.productSize,
                        quantity = 1
                    )
                }
                _cartItems.value = products
                calculateTotalPrice(products) // Update total price after fetching items
            }
        }
    }

    // Increase the quantity of the product in the cart
    fun increaseCartItemQuantity(id: String) {
        _cartItems.getAndUpdate { currentItems ->
            currentItems.map { item ->
                if (item.id == id) {
                    item.copy(quantity = item.quantity + 1)
                } else {
                    item
                }
            }.also { updatedItems ->
                calculateTotalPrice(updatedItems) // Recalculate total price after updating quantity
            }
        }
    }

    // Decrease the quantity of the product in the cart, ensuring it doesn't go below 0
    fun decreaseCartItemQuantity(id: String) {
        _cartItems.getAndUpdate { currentItems ->
            currentItems.map { item ->
                if (item.id == id && item.quantity > 1) { // Prevent going below 0
                    item.copy(quantity = item.quantity - 1)
                } else {
                    item
                }
            }.also { updatedItems ->
                calculateTotalPrice(updatedItems) // Recalculate total price after updating quantity
            }
        }
    }

    // Helper function to calculate total price of all items in the cart
    private fun calculateTotalPrice(items: List<CartProduct>) {
        val total = items.sumBy { it.quantity * it.price.toInt() } // Sum of price * quantity for all items
        _totalPrice.value = total
    }


    fun onLongClicked(itemIndex: Int){
        _dialogIsVisible.value = true
        _clickedItemIndex.value = itemIndex
    }

    fun onDismissDialog() {
        _dialogIsVisible.value = false
        Log.d("DialogState", "Dialog dismissed: ${_dialogIsVisible.value}")
    }


    fun deleteCartItem() {
        viewModelScope.launch {
            val index = _clickedItemIndex.value
            if (index in cartItems.value.indices) {
                val itemToDelete = cartItems.value[index]
                myRepository.deleteCartItem(itemToDelete)
                _dialogIsVisible.value = false
            }
        }
    }

    fun deleteAllCartItems(onTransactionSuccess:(TransactionStatus)-> Unit){
        viewModelScope.launch {
            cartItems.value.forEach {
                myRepository.deleteCartItemById(
                    id = it.id
                )
            }

            onTransactionSuccess(TransactionStatus.SUCCESS)
        }
    }

}







