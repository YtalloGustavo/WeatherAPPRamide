package com.example.weatherapp_ramide

import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth

class WeatherApp : Application() {
    private val flags = FLAG_ACTIVITY_SINGLE_TOP or
        FLAG_ACTIVITY_NEW_TASK or
        FLAG_ACTIVITY_CLEAR_TASK

    override fun onCreate() {
        super.onCreate()

        if (FirebaseApp.getApps(this).isEmpty()) {
            Log.w("WeatherApp", "Firebase nao configurado. Adicione app/google-services.json.")
            goToLogin()
            return
        }

        Firebase.auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                goToMain()
            } else {
                goToLogin()
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java).setFlags(flags))
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).setFlags(flags))
    }
}
