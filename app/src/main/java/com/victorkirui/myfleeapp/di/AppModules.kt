package com.victorkirui.myfleeapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.google.firebase.ktx.Firebase
import com.victorkirui.myfleeapp.data.AppDatabase
import com.victorkirui.myfleeapp.data.CartDao
import com.victorkirui.myfleeapp.data.FavouritesProductsDao
import com.victorkirui.myfleeapp.data.MyRepository
import com.victorkirui.myfleeapp.data.UserDetailsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.datastore by preferencesDataStore(name = "userName")


@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Provides
    @Singleton
    fun provideFirebase(): Firebase{
        return Firebase
    }

    @Provides
    @Singleton
    fun provideMyRepository(firebase: Firebase,
                            cartDao: CartDao,
                            favouritesProductsDao: FavouritesProductsDao): MyRepository {
        return MyRepository(firebase, cartDao, favouritesProductsDao)
    }


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database").build()
    }

    @Provides
    @Singleton
    fun provideCartDao(db: AppDatabase): CartDao{
        return db.cartDao
    }

    @Provides
    @Singleton
    fun provideFavouritesDao(db: AppDatabase): FavouritesProductsDao{
        return db.favouritesProductsDao
    }

    @Provides
    @Singleton
    fun provideAppContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences>{
        return context.datastore
    }

    @Provides
    @Singleton
    fun userDetailsRepository(dataStore: DataStore<Preferences>): UserDetailsRepository {
        return UserDetailsRepository(dataStore)
    }
}