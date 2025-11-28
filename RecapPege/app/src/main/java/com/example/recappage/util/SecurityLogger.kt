package com.example.recappage.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object SecurityLogger {

    fun logLoginActivity() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .collection("login_logs")
            .add(mapOf("timestamp" to System.currentTimeMillis()))
    }

    fun logSuspiciousActivity(reason: String, action: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .collection("suspicious_logs")
            .add(
                mapOf(
                    "reason" to reason,
                    "action" to action,
                    "timestamp" to System.currentTimeMillis()
                )
            )
    }
}
