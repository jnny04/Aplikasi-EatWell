package com.example.recappage.ui.register


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // 👈 Tambahkan import clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.ui.theme.SourceSans3 // Merujuk ke FontFamily yang Anda definisikan


import com.google.firebase.auth.FirebaseAuth // ✅ Baru
import androidx.compose.ui.platform.LocalContext // ✅ Baru
import android.widget.Toast // ✅ Baru untuk notifikasi error
import com.example.recappage.R
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavHostController) {

    // Pindahkan State Input ke sini agar bisa diakses oleh Tombol Sign In
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Inisialisasi Firebase Auth
    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current // Dapatkan konteks

    val containerColor = Color(0xFFD3D3D3) // Light gray color

    // Fungsi untuk Log In
    val performSignIn: () -> Unit = {
        // 1. Cek apakah email adalah akun Gmail
        if (!email.endsWith("@gmail.com", ignoreCase = true)) {
            Toast.makeText(context, "Hanya akun Gmail (@gmail.com) yang diizinkan untuk masuk.", Toast.LENGTH_LONG).show()
        } else {
            // 2. Lakukan Sign In dengan Firebase Email/Password
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Login Berhasil
                        Toast.makeText(context, "Selamat datang kembali!", Toast.LENGTH_SHORT).show()

                        // PERBAIKAN 1: Gunakan rute yang benar dan bungkus dalam run
                        run {
                            navController.navigate(Screen.HomePage.route) { // Gunakan Screen.HomePage.route
                                popUpTo(Screen.SignIn.route) { inclusive = true } // Gunakan Screen.SignIn.route
                            }
                        }
                    } else {
                        // Login Gagal (misal: email/password salah)
                        Toast.makeText(context, "Login Gagal. Cek kembali email dan password Anda.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(), // 👈 .background(Color.White) DIHAPUS DARI SINI

        // 🔽 PINDAHKAN TopBorder KE SLOT 'topBar' 🔽
        topBar = {
            TopBorder(
                navController = navController,
                showProfile = false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // 👈 Padding ini sekarang sudah benar (dimulai di BAWAH TopBorder)
                .background(Color.White), // 👈 .background(Color.White) PINDAH KE SINI
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // ❌ TopBorder(...) DIHAPUS DARI SINI


            // Spacer atas biar konten turun ke tengah
            // 🔹 Spacer atas
            Spacer(modifier = Modifier.weight(0.1f)) // <-- Kecilkan weight untuk geser ke atas

            // 🔽 TAMBAHKAN KODE DI BAWAH INI 🔽
            Image(
                painter = painterResource(id = R.drawable.eatwelllogo),
                contentDescription = "Eatwell Logo",
                modifier = Modifier.height(150.dp) // <-- Sesuaikan ukuran tinggi (60.dp) sesuai kebutuhan
            )

            // Tambahkan spasi antara logo dan teks "Welcome Back!"
            Spacer(modifier = Modifier.height(16.dp))
            // 🔼 BATAS AKHIR KODE TAMBAHAN 🔼


            // 🔹 Welcome Text
            WelcomeText()

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Form Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                // Group: Field + Forgot Password
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // **START: Konten Form Input (Pengganti LoginForm)**
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Email Input Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.emailicon),
                                    contentDescription = "Email Icon",
                                    tint = Color.Gray
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = containerColor,
                                unfocusedContainerColor = containerColor,
                                cursorColor = Color.Gray,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray,
                                focusedLeadingIconColor = Color.Gray,
                                unfocusedLeadingIconColor = Color.Gray
                            )
                        )

                        // Password Input Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.pswordicon),
                                    contentDescription = "Password Icon",
                                    tint = Color.Gray
                                )
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val imageVector = if (passwordVisible)
                                    Icons.Filled.VisibilityOff
                                else
                                    Icons.Filled.Visibility

                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = imageVector, contentDescription = "Toggle password visibility", tint = Color.Gray)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = containerColor,
                                unfocusedContainerColor = containerColor,
                                cursorColor = Color.Gray,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray,
                                focusedLeadingIconColor = Color.Gray,
                                unfocusedLeadingIconColor = Color.Gray
                            )
                        )
                    }
                    // **END: Konten Form Input**

                    // Forgot Password
                    Text(
                        text = "Forgot Password?",
                        color = Color(0xFFFC7100),
                        fontSize = 10.sp,
                        style = TextStyle(
                            fontFamily = SourceSans3,
                            fontWeight = FontWeight.SemiBold, // Semibold
                            textDecoration = TextDecoration.Underline // ✅ Underline
                        ),

                        modifier = Modifier
                            .align(Alignment.End)
                            .offset(y = (4).dp, x = (-5).dp)
                            .clickable {
                                // TODO: Implementasi navigasi Forgot Password
                            }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign In Button
                HijauImage(onClick = performSignIn)
            }

            // Spacer bawah biar tetap center-ish
            Spacer(modifier = Modifier.weight(1f))

            // 🔹 Bottom Register Text
            BottomRegisterText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                navController = navController
            )
        }
    }
}


@Composable
fun WelcomeText() {
    Text(
        text = "Welcome Back!",
        // Ubah warna ke FC7100 (Orange)
        color = Color(0xFFFC7100),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        // Terapkan font kustom (asumsi sudah didefinisikan)
        style = TextStyle(
            // Ganti 'SourceSerifPro' dengan nama FontFamily yang sudah Anda definisikan
            fontFamily = SourceSerifPro
        )
    )
}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LoginForm() {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var passwordVisible by remember { mutableStateOf(false) }
//
//    val containerColor = Color(0xFFD3D3D3) // Light gray color
//
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        // Email Input Field
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            leadingIcon = {
//                Icon(
//                    painter = painterResource(id = R.drawable.emailicon),
//                    contentDescription = "Email Icon",
//                    tint = Color.Gray
//                )
//            },
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(8.dp),
//            colors = OutlinedTextFieldDefaults.colors( // <-- Perbaikan di sini
//                focusedBorderColor = Color.Transparent,
//                unfocusedBorderColor = Color.Transparent,
//                focusedContainerColor = containerColor,
//                unfocusedContainerColor = containerColor,
//                cursorColor = Color.Gray, // Optional: for cursor color
//                focusedLabelColor = Color.Gray,
//                unfocusedLabelColor = Color.Gray,
//                focusedLeadingIconColor = Color.Gray,
//                unfocusedLeadingIconColor = Color.Gray
//            )
//        )
//
//        // Password Input Field
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            leadingIcon = {
//                Icon(
//                    painter = painterResource(id = R.drawable.pswordicon),
//                    contentDescription = "Password Icon",
//                    tint = Color.Gray
//                )
//            },
//            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//            trailingIcon = {
//                val imageVector = if (passwordVisible)
//                    Icons.Filled.VisibilityOff
//                else
//                    Icons.Filled.Visibility
//
//                IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                    Icon(imageVector = imageVector, contentDescription = "Toggle password visibility", tint = Color.Gray)
//                }
//            },
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(8.dp),
//            colors = OutlinedTextFieldDefaults.colors( // <-- Perbaikan di sini
//                focusedBorderColor = Color.Transparent,
//                unfocusedBorderColor = Color.Transparent,
//                focusedContainerColor = containerColor,
//                unfocusedContainerColor = containerColor,
//                cursorColor = Color.Gray, // Optional: for cursor color
//                focusedLabelColor = Color.Gray,
//                unfocusedLabelColor = Color.Gray,
//                focusedLeadingIconColor = Color.Gray,
//                unfocusedLeadingIconColor = Color.Gray
//            )
//        )
//    }
//}
@Composable
// 🎯 PERUBAHAN 4: Tambahkan parameter onClick dan buat Box clickable
fun HijauImage(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
            .clickable(onClick = onClick), // 👈 Memicu navigasi
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.hijau),
            contentDescription = "Hijau Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        // Tambahkan Teks "Sign In" di atas tombol
//        Text(
//            text = "Sign In",
//            color = Color.White,
//            fontWeight = FontWeight.Bold,
//            fontSize = 18.sp
//        )
    }
}


@Composable
fun BottomRegisterText(modifier: Modifier = Modifier, navController: NavHostController) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Black, fontSize = 16.sp)) {
            append("Don’t have an account? ")
        }
        pushStringAnnotation(tag = "REGISTER", annotation = "register_screen")

        // Perubahan dilakukan di sini: Tambahkan textDecoration = TextDecoration.Underline
        withStyle(style = SpanStyle(
            color = Color(0xFFFC7100),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textDecoration = TextDecoration.Underline // ✅ Tambahkan garis bawah
        )) {
            append("Register now")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        modifier = modifier,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "REGISTER", start = offset, end = offset)
                .firstOrNull()?.let {
                    navController.navigate(it.item)
                }
        }
    )
}


// ⚠️ Catatan: Fungsi MainScreenInHomePage ada di file HomePage.kt Anda.
// Pastikan file tersebut sudah di-update dan dipindahkan ke package yang sama.
// Composable wrapper untuk HomePage.kt (agar NavHost bisa memanggil Home Page Anda)
