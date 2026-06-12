package com.example.weatherapp_ramide.db.fb

import android.util.Log
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange

class FBDatabase {
    interface Listener {
        fun onUserLoaded(user: FBUser)
        fun onUserSignOut()
        fun onCityAdded(city: FBCity)
        fun onCityUpdated(city: FBCity)
        fun onCityRemoved(city: FBCity)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var citiesListReg: ListenerRegistration? = null
    private var listener: Listener? = null

    init {
        auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                citiesListReg?.remove()
                listener?.onUserSignOut()
                return@addAuthStateListener
            }
            val refCurrUser = db.collection("users").document(auth.currentUser!!.uid)
            refCurrUser.get().addOnSuccessListener {
                it.toObject(FBUser::class.java)?.let { user ->
                    listener?.onUserLoaded(user)
                }
            }
            citiesListReg = refCurrUser.collection("cities")
                .addSnapshotListener { snapshots, ex ->
                    if (ex != null) {
                        Log.e("FBDatabase", "Error listening to cities", ex)
                        return@addSnapshotListener
                    }
                    snapshots?.documentChanges?.forEach { change ->
                        val fbCity = change.document.toObject(FBCity::class.java)
                        when (change.type) {
                            DocumentChange.Type.ADDED -> listener?.onCityAdded(fbCity)
                            DocumentChange.Type.MODIFIED -> listener?.onCityUpdated(fbCity)
                            DocumentChange.Type.REMOVED -> listener?.onCityRemoved(fbCity)
                        }
                    }
                }
        }
    }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
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
    }
}
