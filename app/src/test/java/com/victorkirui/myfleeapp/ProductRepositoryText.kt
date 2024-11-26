package com.victorkirui.myfleeapp

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.tasks.TaskCompletionSource
import com.victorkirui.myfleeapp.data.MyRepository
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify

class ProductRepositoryTest {

    private lateinit var firebase: Firebase
    private lateinit var productRepository: MyRepository

    @Before
    fun setUp() {
        // Initialize Firebase
        firebase = mock()

        // Initialize MyRepository with Firebase
        productRepository = MyRepository(firebase)
    }

    @Test
    fun `test fetch products from Firestore`(): Unit = runBlocking {
        // Access Firestore via Firebase instance
        val firestore = Firebase.firestore

        // Create a mock CollectionReference for products
        val productsCollection: CollectionReference = mock()

        // Mock the Firestore collection reference to return our mock data
        whenever(firestore.collection("products")).thenReturn(productsCollection)

        // Mock a query snapshot
        val mockQuerySnapshot: QuerySnapshot = mock()
        val mockDocumentSnapshot: DocumentSnapshot = mock()

        // Simulating data fetched from Firestore
        whenever(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

        // Create a TaskCompletionSource to mock the Task<QuerySnapshot>
        val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
        taskCompletionSource.setResult(mockQuerySnapshot)  // Set the result to the mock QuerySnapshot

        // Mock the productsCollection.get() to return the Task
        whenever(productsCollection.get()).thenReturn(taskCompletionSource.task)

        // Call the repository function
        productRepository.getMostPopularProductsFlow()

        // Verify that the correct Firestore method was called
        verify(firestore).collection("products")
        verify(productsCollection).get()
    }
}
