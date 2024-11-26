package com.victorkirui.myfleeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemsEntity(

    @PrimaryKey
    val id: String = "0",

    val productName: String,

    val price: Int,

    val imageUrl: String,

    val productSize: String,

    val productDescription: String
)

