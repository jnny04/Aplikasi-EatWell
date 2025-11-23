package com.example.recappage.ui.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.draw.shadow
import com.example.recappage.ui.navigation.Screen
import com.example.recappage.R // ✅ Pastikan import R ini ada
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPassScreen(navController: NavHostController) { // ✅ Nama fungsi disesuaikan dengan Navigation.kt (ForgotPassScreen)

    // State hanya untuk Email (Password baru diatur via link email)
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Loading state agar user tidak klik berkali-kali

    // Constants & Resources
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    // --- WARNA & STYLE ---
    val orangeColor = Color(0xFFFC7100)
    val inputBackgroundColor = Color(0xFFD3D3D3)
    val submitButtonColor = Color(0xFF689D5E)
    val placeholderColor = Color(0xFF9C9C9C)
    val buttonTextColor = Color(0xFFFCFCFC)

    val inputTextStyle = TextStyle(
        fontFamily = SourceSans3,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = Color.Black // Warna teks saat mengetik sebaiknya hitam/gelap
    )

    val buttonTextStyle = TextStyle(
        fontFamily = SourceSans3,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = buttonTextColor
    )

    // ✅ FUNGSI UTAMA: Kirim Email Reset
    val performPasswordReset: () -> Unit = {
        if (email.isBlank()) {
            Toast.makeText(context, "Mohon masukkan email Anda.", Toast.LENGTH_SHORT).show()
        } else {
            isLoading = true // Mulai loading

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    isLoading = false // Selesai loading

                    if (task.isSuccessful) {
                        // BERHASIL
                        Toast.makeText(
                            context,
                            "Link reset password telah dikirim ke email $email. Silakan cek Inbox/Spam.",
                            Toast.LENGTH_LONG
                        ).show()

                        // Kembali ke halaman Login
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(Screen.ForgotPass.route) { inclusive = true }
                        }
                    } else {
                        // GAGAL (Misal email tidak terdaftar atau format salah)
                        val errorMsg = task.exception?.localizedMessage ?: "Terjadi kesalahan."
                        Toast.makeText(context, "Gagal: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.4f))

            // 1. Ikon Tanda Seru
            Image(
                painter = painterResource(id = R.drawable.tandaseru),
                contentDescription = "Warning Icon",
                modifier = Modifier.size(90.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Judul
            Text(
                text = "Forgot Password?",
                color = orangeColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(fontFamily = SourceSerifPro)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Penjelasan Flow (Penting agar user paham)
            Text(
                text = "Enter your email below. We will send a link to reset your password via email.",
                color = Color(0xFF555555),
                fontSize = 10.sp,
                style = TextStyle(
                    fontFamily = SourceSans3,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4. HANYA INPUT EMAIL (Password dihapus)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter Email", style = TextStyle(color = placeholderColor, fontFamily = SourceSans3)) },
                textStyle = inputTextStyle,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.emailicon),
                        contentDescription = null,
                        tint = placeholderColor
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = inputBackgroundColor,
                    unfocusedContainerColor = inputBackgroundColor,
                    cursorColor = orangeColor
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 5. Tombol Submit
            Button(
                onClick = {
                    if (!isLoading) performPasswordReset()
                },
                colors = ButtonDefaults.buttonColors(containerColor = submitButtonColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp)
                    .shadow(2.dp, shape = RoundedCornerShape(8.dp))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Submit",
                        style = buttonTextStyle
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Tombol Back
            Text(
                text = "< Back to login",
                color = orangeColor,
                fontSize = 12.sp,
                style = TextStyle(
                    fontFamily = SourceSans3,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}