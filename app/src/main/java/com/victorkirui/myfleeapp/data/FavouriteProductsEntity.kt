package com.victorkirui.myfleeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class FavouriteProductsEntity(
    @PrimaryKey
    val id: String = "0",

    val productName: String,

    val price: Int,

    val imageUrl: String,

    val productSizes: List<String>,

    val productDescription: String
)