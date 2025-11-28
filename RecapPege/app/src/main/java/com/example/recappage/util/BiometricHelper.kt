package com.example.recappage.util

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricHelper {

    // Fungsi untuk mengecek apakah HP mendukung Biometrik
    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    // Fungsi untuk memunculkan Dialog Sidik Jari
    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // 1. Siapkan Executor (yang menjalankan tugas di UI thread)
        val executor = ContextCompat.getMainExecutor(activity)

        // 2. Siapkan Callback (Apa yang terjadi jika sukses/gagal)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess() // Panggil fungsi sukses
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString()) // Panggil fungsi error
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Gagal mengenali jari (misal jari basah), tapi belum error total
                // Biasanya kita diamkan saja agar user mencoba lagi
            }
        }

        // 3. Buat Prompt (Dialognya)
        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        // 4. Konfigurasi Tampilan Dialog
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login to EatWell")
            .setSubtitle("Use your fingerprint or face to login")
            .setNegativeButtonText("Cancel")
            .build()

        // 5. Tampilkan!
        biometricPrompt.authenticate(promptInfo)
    }
}