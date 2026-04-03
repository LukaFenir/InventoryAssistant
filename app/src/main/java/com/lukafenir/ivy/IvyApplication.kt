package com.lukafenir.ivy

import android.app.Application
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class IvyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                Log.d("IvyApplication", "User signed in with UID: $uid")
            }
            .addOnFailureListener { e ->
                Log.e("IvyApplication", "Error signing in anonymously", e)
            }
    }
}