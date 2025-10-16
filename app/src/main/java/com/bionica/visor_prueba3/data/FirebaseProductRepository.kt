package com.bionica.visor_prueba3.data


import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID


class FirebaseProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()


    private val productsCol get() = db.collection("products")


    suspend fun addProduct(
        name: String,
        price: Double,
        unit: String,
        description: String,
        imageUri: Uri?
    ) {
        val uid = auth.currentUser?.uid ?: ""
        var imageUrl = ""
        if (imageUri != null) {
            val ref = storage.reference.child("products/${UUID.randomUUID()}.jpg")
            ref.putFile(imageUri).await()
            imageUrl = ref.downloadUrl.await().toString()
        }
        val docRef = productsCol.document()
        val product = Product(
            id = docRef.id,
            name = name.trim(),
            price = price,
            unit = unit.trim(),
            description = description.trim(),
            imageUrl = imageUrl,
            ownerUid = uid,
            createdAt = System.currentTimeMillis()
        )
        docRef.set(product).await()
    }
}