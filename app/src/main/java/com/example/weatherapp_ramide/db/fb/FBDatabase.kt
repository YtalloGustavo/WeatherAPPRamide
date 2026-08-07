package com.example.weatherapp_ramide.db.fb

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow

class FBDatabase {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    val user: Flow<FBUser>
        get() {
            val uid = auth.currentUser?.uid ?: return emptyFlow()
            return callbackFlow {
                val listener = db.collection("users").document(uid)
                    .addSnapshotListener { doc, error ->
                        if (error != null) {
                            Log.e("FBDatabase", "Erro ao ler usuario", error)
                            return@addSnapshotListener
                        }
                        doc?.toObject(FBUser::class.java)?.let { trySend(it) }
                    }
                awaitClose { listener.remove() }
            }
        }

    val cities: Flow<List<FBCity>>
        get() {
            val uid = auth.currentUser?.uid ?: return emptyFlow()
            return callbackFlow {
                val listener = db.collection("users").document(uid)
                    .collection("cities")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("FBDatabase", "Error listening to cities", error)
                            return@addSnapshotListener
                        }
                        snapshot?.let { trySend(it.toObjects(FBCity::class.java)) }
                    }
                awaitClose { listener.remove() }
            }
        }

    fun register(user: FBUser) {
        val uid = auth.currentUser?.uid ?: run {
            Log.e("FBDatabase", "register() called but user not logged in")
            return
        }
        db.collection("users").document(uid).set(user)
    }

    fun add(city: FBCity) {
        val uid = auth.currentUser?.uid ?: run {
            Log.e("FBDatabase", "add() called but user not logged in")
            return
        }
        if (city.name.isNullOrEmpty()) {
            Log.e("FBDatabase", "add() called with null or empty city name")
            return
        }
        db.collection("users").document(uid).collection("cities")
            .document(city.name!!).set(city)
            .addOnFailureListener { e -> Log.e("FBDatabase", "Erro ao adicionar cidade", e) }
    }

    fun remove(city: FBCity) {
        val uid = auth.currentUser?.uid ?: run {
            Log.e("FBDatabase", "remove() called but user not logged in")
            return
        }
        if (city.name.isNullOrEmpty()) {
            Log.e("FBDatabase", "remove() called with null or empty city name")
            return
        }
        db.collection("users").document(uid).collection("cities")
            .document(city.name!!).delete()
            .addOnFailureListener { e -> Log.e("FBDatabase", "Erro ao remover cidade", e) }
    }

    fun update(city: FBCity) {
        if (auth.currentUser == null) throw RuntimeException("Not logged in!")
        val uid = auth.currentUser!!.uid
        val changes = mapOf(
            "lat" to city.lat,
            "lng" to city.lng,
            "monitored" to city.monitored
        )
        db.collection("users").document(uid)
            .collection("cities").document(city.name!!).update(changes)
    }
}