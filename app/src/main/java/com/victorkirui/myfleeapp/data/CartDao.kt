package com.victorkirui.myfleeapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface CartDao {

    @Insert
    suspend fun insertCartItem(cartItemsEntity: CartItemsEntity)

    @Update
    suspend fun updateCartItem(cartItemsEntity: CartItemsEntity)

    @Delete
    suspend fun deleteCartItem(cartItemsEntity: CartItemsEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItemById(id: String)

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemsEntity>>
}