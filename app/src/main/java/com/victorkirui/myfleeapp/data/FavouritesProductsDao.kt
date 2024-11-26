package com.victorkirui.myfleeapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface FavouritesProductsDao {

    @Insert
    suspend fun insertCartItem(favouriteProductsEntity: FavouriteProductsEntity)

    @Update
    suspend fun updateCartItem(favouriteProductsEntity: FavouriteProductsEntity)

    @Delete
    suspend fun deleteCartItem(favouriteProductsEntity: FavouriteProductsEntity)

    @Query("SELECT * FROM favouriteproductsentity")
    fun getAllFavouriteItems(): Flow<List<FavouriteProductsEntity>>
}