package com.example.recappage.util

import android.util.Log

object ImageHelper {

    fun optimizeUrl(originalUrl: String?): String {
        if (originalUrl.isNullOrEmpty()) return ""

        val optimizedUrl = originalUrl
            .replace("-556x370.jpg", "-312x231.jpg")
            .replace("-556x370.png", "-312x231.png")
            .replace("-636x393.jpg", "-312x231.jpg")
            .replace("-636x393.png", "-312x231.png")

        if (originalUrl != optimizedUrl) {
            Log.d("PAYLOAD_OPTIMIZER", "⚡ Menghemat Data: \nAsli: $originalUrl \nBaru: $optimizedUrl")
        }
        return optimizedUrl
    }
}