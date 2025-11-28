package com.example.recappage.ui.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.example.recappage.R
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.navigation.Screen
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.util.BiometricHelper
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavHostController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current

    // --- BIOMETRIK SETUP ---
    val fragmentActivity = context as? FragmentActivity
    val isBiometricAvailable = remember { BiometricHelper.isBiometricAvailable(context) }
    // Cek apakah user pernah login sebelumnya untuk menampilkan tombol
    val showBiometricButton = isBiometricAvailable && auth.currentUser != null

    val performBiometricLogin = {
        if (fragmentActivity != null) {
            BiometricHelper.showBiometricPrompt(
                activity = fragmentActivity,
                onSuccess = {
                    Toast.makeText(context, "Login Biometrik Berhasil!", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.HomePage.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onError = { errorMsg ->
                    Toast.makeText(context, "Gagal: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    val containerColor = Color(0xFFD3D3D3)

    val performSignIn: () -> Unit = {
        if (!email.endsWith("@gmail.com", ignoreCase = true)) {
            Toast.makeText(context, "Hanya akun Gmail (@gmail.com) yang diizinkan untuk masuk.", Toast.LENGTH_LONG).show()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Selamat datang kembali!", Toast.LENGTH_SHORT).show()
                        run {
                            navController.navigate(Screen.HomePage.route) {
                                popUpTo(Screen.SignIn.route) { inclusive = true }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Login Gagal. Cek kembali email dan password Anda.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBorder(navController = navController, showProfile = false)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.weight(0.1f))

            Image(
                painter = painterResource(id = R.drawable.eatwelllogo),
                contentDescription = "Eatwell Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            WelcomeText()

            Spacer(modifier = Modifier.height(30.dp))

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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Email Input
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

                        // Password Input
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
                                val imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = imageVector, contentDescription = "Toggle visibility", tint = Color.Gray)
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

                    // Forgot Password
                    Text(
                        text = "Forgot Password?",
                        color = Color(0xFFFC7100),
                        fontSize = 10.sp,
                        style = TextStyle(
                            fontFamily = SourceSans3,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .align(Alignment.End)
                            .offset(y = (4).dp, x = (-5).dp)
                            .clickable { navController.navigate(Screen.ForgotPass.route) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ✅ UPDATE LAYOUT: Sign In & Fingerprint Sebelahan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 1. Tombol Sign In (Hijau Image)
                    HijauImage(onClick = performSignIn)

                    // 2. Tombol Biometrik (Hanya muncul jika syarat terpenuhi)
                    if (showBiometricButton) {
                        Spacer(modifier = Modifier.width(5.dp))

                        // 🔥 PERBAIKAN DI SINI: Background box dihapus dan icon diperbesar
                        Box(
                            modifier = Modifier
                                .size(50.dp) // Ukuran area sentuh tetap agar mudah diklik
                                .clip(RoundedCornerShape(12.dp))
                                // .background(Color(0xFFE8F5E9)) <-- DIHAPUS agar tidak ada kotak
                                .clickable { performBiometricLogin() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.fingerprint),
                                contentDescription = "Biometric Login",
                                tint = Color(0xFF5CA135), // Warna Hijau Fingerprint tetap
                                modifier = Modifier.size(48.dp) // 🔥 Icon DIPERBESAR (dari 32 ke 48)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

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
        color = Color(0xFFFC7100),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        style = TextStyle(fontFamily = SourceSerifPro)
    )
}

@Composable
fun HijauImage(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.hijau),
            contentDescription = "Hijau Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}


@Composable
fun BottomRegisterText(modifier: Modifier = Modifier, navController: NavHostController) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Black, fontSize = 16.sp)) {
            append("Don’t have an account? ")
        }
        pushStringAnnotation(tag = "REGISTER", annotation = "register_screen")

        withStyle(style = SpanStyle(
            color = Color(0xFFFC7100),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textDecoration = TextDecoration.Underline
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