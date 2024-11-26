package com.victorkirui.myfleeapp.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victorkirui.myfleeapp.data.MyRepository
import com.victorkirui.myfleeapp.data.Product
import com.victorkirui.myfleeapp.data.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MyRepository,
    private val context: Context
) : ViewModel() {

    // StateFlows to expose data to the UI
    private val _popularProducts = MutableStateFlow<List<Product>>(emptyList())
    val popularProducts: StateFlow<List<Product>> = _popularProducts.asStateFlow()

    private val _newArrivals = MutableStateFlow<List<Product>>(emptyList())
    val newArrivals: StateFlow<List<Product>> = _newArrivals.asStateFlow()

    private val _bestSellingStores = MutableStateFlow<List<Store>>(emptyList())
    val bestSellingStores: StateFlow<List<Store>> = _bestSellingStores.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isConnectedToInternet = MutableStateFlow(false)
    val isConnectedToInternet = _isConnectedToInternet.asStateFlow()

    private val _showOfflineWarning = MutableStateFlow(false)
    val showOfflineWarning = _showOfflineWarning.asStateFlow()

    private val _showNoDataWarning = MutableStateFlow(false)
    val showNoDataWarning = _showNoDataWarning.asStateFlow()

    private val _searchBarText = MutableStateFlow("")
    val searchBarText = _searchBarText.asStateFlow()

    private val _searchedProducts = MutableStateFlow<List<Product>>(emptyList())
    val searchedProducts = _searchedProducts.asStateFlow()

    // Perform search when user clicks the search button
    fun onSearchButtonClicked() {
        val query = _searchBarText.value
        viewModelScope.launch {
            if (query.isNotEmpty()) {
                // Filter products based on the search query
                val filteredProducts = _popularProducts.value.filter {
                    it.productName.contains(query, ignoreCase = true)
                }
                _searchedProducts.value = filteredProducts
            } else {
                _searchedProducts.value = emptyList() // Clear search results if the query is empty
            }
        }
    }

    init {
        // First, check internet connectivity
        isOnline()

        // Check for cached data and decide the UI state
        checkCachedData()


    }

    fun onSearchBarTextChange(text: String){
        _searchBarText.value = text
    }

    // Check if the device is online
    private fun isOnline() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        _isConnectedToInternet.value = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Check if there's cached data
    private fun checkCachedData() {
        // Check if FireStore has cached data available
        if (_isConnectedToInternet.value) {
            fetchPopularProducts()
            fetchNewArrivals()
            fetchBestSellingStores()
        } else {
            // If not connected to the internet, check for cached data
            if (popularProducts.value.isEmpty() && newArrivals.value.isEmpty() && bestSellingStores.value.isEmpty()) {
                // If there's no cached data, show "no data" warning
                _showNoDataWarning.value = true
            } else {
                // If cached data is present, show offline warning
                _showOfflineWarning.value = true
            }
        }
    }

    // Fetch popular products as a flow
    private fun fetchPopularProducts() {
        viewModelScope.launch {
            repository.getMostPopularProductsFlow()
                .catch { exception -> _error.value = exception.message }
                .collect { products ->
                    _popularProducts.value = products
                    _showNoDataWarning.value = false // Hide "no data" warning when data is fetched
                }
        }
    }

    // Fetch new arrivals as a flow
    private fun fetchNewArrivals() {
        viewModelScope.launch {
            repository.getNewArrivalsFlow()
                .catch { exception -> _error.value = exception.message }
                .collect { products ->
                    _newArrivals.value = products
                    _showNoDataWarning.value = false // Hide "no data" warning when data is fetched
                }
        }
    }

    // Fetch best-selling stores as a flow
    private fun fetchBestSellingStores() {
        viewModelScope.launch {
            repository.getBestSellingStoresFlow()
                .catch { exception -> _error.value = exception.message }
                .collect { stores ->
                    _bestSellingStores.value = stores
                    _showNoDataWarning.value = false // Hide "no data" warning when data is fetched
                }
        }
    }

    fun clearSearchProducts(){
        _searchedProducts.value = emptyList()
    }
}

