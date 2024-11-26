package com.victorkirui.myfleeapp.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import com.victorkirui.myfleeapp.domain.TransactionStatus
import com.victorkirui.myfleeapp.ui.cart.CartProduct
import com.victorkirui.myfleeapp.ui.favourites.FavouriteProducts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MyRepository(private val firebase: Firebase,
    private val cartDao: CartDao,
    private val favouritesProductsDao: FavouritesProductsDao) {

    private val firestore = firebase.firestore

    fun getMostPopularProductsFlow(): Flow<List<Product>> {
        return firestore.collection("products")
            .orderBy("popularity", Query.Direction.DESCENDING)
            .snapshots().map { snapshot ->
                snapshot.documents.map { document ->
                    val product = document.toObject(Product::class.java)!!
                    product.id = document.id // Assign document ID to product.id
                    product
                }
            }
            .catch { exception ->
                Log.e("Firestore", "Error retrieving products", exception)
                emit(emptyList())
            }
    }

    fun getNewArrivalsFlow(): Flow<List<Product>> {
        return firestore.collection("products")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()  // Firestore snapshots as a Flow
            .map { snapshot ->
                snapshot.documents.map { document ->
                    val product = document.toObject(Product::class.java)!!
                    product.id = document.id // Assign document ID to product.id
                    product
                }
            }
            .catch { exception ->
                Log.e("Firestore", "Error retrieving new arrivals", exception)
                emit(emptyList())  // Emit an empty list in case of an error
            }
    }

    fun getBestSellingStoresFlow(): Flow<List<Store>> = flow {
        val snapshot = firestore.collection("stores")
            .orderBy("totalSales", Query.Direction.DESCENDING)
            .get()
            .await()
        emit(snapshot.toObjects(Store::class.java))
    }

    fun getProductDetails(id: String): Flow<Product?> = flow {
        try {
            val snapshot = firestore.collection("products")
                .document(id)  // Match the 'id' field
                .get()
                .await()

            // Log the snapshot to verify data
            Log.d("Repository", "Snapshot fetched: ${snapshot.id} documents")

            // Convert the document snapshot to a Product object
            val product = snapshot.toObject(Product::class.java)
            emit(product)  // Emit the product (or null if not found)
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching product details", e)
            emit(null)  // Emit null in case of an error
        }
    }

    //Cart Entity functions
    suspend fun insertCartItem(product: CartProduct){
        cartDao.insertCartItem(
            CartItemsEntity(
                id = product.id,
                productName = product.productName,
                price = product.price.toInt(),
                productDescription = product.description,
                productSize = product.sizes,
                imageUrl = product.imageUrl
            )
        )
    }

    suspend fun deleteCartItem(product: CartProduct){
        cartDao.deleteCartItem(
            CartItemsEntity(
                id = product.id,
                productName = product.productName,
                price = product.price.toInt(),
                productDescription = product.description,
                productSize = product.sizes,
                imageUrl = product.imageUrl
            )
        )
    }

    suspend fun deleteCartItemById(id: String) {
        cartDao.deleteCartItemById(id)
    }

    fun getAllCartItems(): Flow<List<CartItemsEntity>>{
        return cartDao.getAllCartItems()
    }


    //Favourites Entity functions
    suspend fun insertFavourites(product: FavouriteProducts){
        favouritesProductsDao.insertCartItem(
            FavouriteProductsEntity(
                id = product.id,
                productName = product.productName,
                price = product.price.toInt(),
                productSizes = product.sizes,
                productDescription = product.description,
                imageUrl = product.imageUrl
            )
        )
    }

    suspend fun deleteFavourites(product: FavouriteProducts){
        favouritesProductsDao.deleteCartItem(
            FavouriteProductsEntity(
                id = product.id,
                productName = product.productName,
                price = product.price.toInt(),
                productSizes = product.sizes,
                productDescription = product.description,
                imageUrl = product.imageUrl
            )
        )
    }

    fun getFavouriteItems(): Flow<List<FavouriteProductsEntity>> {
        return favouritesProductsDao.getAllFavouriteItems()
    }


    suspend fun addFavouriteToFirebase(favouriteProducts: FavouriteProducts): TransactionStatus {
        return suspendCoroutine { continuation ->
            val favouritesDetails = mapOf("id" to favouriteProducts.id)

            firestore.collection("favourites")
                .add(favouritesDetails)
                .addOnSuccessListener {
                    continuation.resume(TransactionStatus.SUCCESS)
                }
                .addOnFailureListener {
                    continuation.resume(TransactionStatus.FAILURE)
                }
        }
    }

    suspend fun removeFavouriteToFirebase(favouriteProducts: FavouriteProducts): TransactionStatus {
        return suspendCoroutine { continuation ->
            firestore.collection("favourites")
                .document(firebase.auth.currentUser!!.uid)
                .update("products", FieldValue.arrayRemove(favouriteProducts.id)) // Assuming "products" is an array
                .addOnSuccessListener {
                    continuation.resume(TransactionStatus.SUCCESS)
                }
                .addOnFailureListener { exception ->
                    // Handle error, e.g., log or show an error message
                    continuation.resume(TransactionStatus.FAILURE)
                }
        }
    }

}


data class Product(
    var id: String = "",
    val productName: String = "",
    val price: String = "0.0",
    val description: String = "",
    val category: String = "",
    val sizes: List<String> = emptyList(),
    val imageUrl: String = "",
    val popularity: Int = 0
)

data class Store(
    var id: String = "",
    val storeName: String = "",
    val totalSales: Double = 0.0,
    val category: String = "",
    val location: String = "",
    val imageUrl: String = ""
)

