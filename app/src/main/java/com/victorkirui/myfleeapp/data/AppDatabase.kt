package com.victorkirui.myfleeapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [CartItemsEntity::class, FavouriteProductsEntity::class], version = 1, exportSchema = false)
@TypeConverters(ProductSizesConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract val cartDao: CartDao

    abstract val favouritesProductsDao: FavouritesProductsDao
}