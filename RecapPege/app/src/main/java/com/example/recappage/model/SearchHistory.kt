package com.example.recappage.model

import com.google.firebase.Timestamp

data class SearchHistory(
    val query: String = "",
    val timestamp: Timestamp = Timestamp.now() // Untuk mengurutkan history nanti
)