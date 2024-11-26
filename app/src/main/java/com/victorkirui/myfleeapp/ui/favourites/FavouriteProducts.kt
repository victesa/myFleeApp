package com.victorkirui.myfleeapp.ui.favourites

data class FavouriteProducts(
    var id: String,
    var price: String,
    var productName: String,
    var description: String,
    var imageUrl: String,
    var sizes: List<String>,
)
